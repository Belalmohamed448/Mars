package lab5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {

    public static void main(String[] args) {
        // ---- the five required scenarios (spec section 12) ----
        scenario1SuccessfulExploration();
        scenario2MissionRejection();
        scenario3ResourceShortage();
        scenario4EmergencyHandling();
        scenario5EnvironmentalEffect();

        scenario6Research();

        // ---- extra unit-style checks ----
        runResourceTests();
        runCrewTests();
    }

    // =====================================================
    // Helpers
    // =====================================================

    private static void title(String text) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(text);
        System.out.println("==================================================");
    }

    private static ArrayList<String> skills(String... s) {
        return new ArrayList<>(Arrays.asList(s));
    }

    // a fresh colony per scenario, so scenarios never affect each other
    private static Colony buildColony() {
        Colony colony = new Colony();

        Habitat habitat = new Habitat("H1", "Habitat A");
        Laboratory lab = new Laboratory("L1", "Laboratory");
        PowerStation power = new PowerStation("P1", "Power Station");
        CommunicationStation comms = new CommunicationStation("C1", "Comm Station");

        habitat.addResource("OXYGEN", 300);
        habitat.addResource("WATER", 100);
        habitat.addResource("FOOD", 100);
        lab.addResource("SPARE_PARTS", 10);
        power.addResource("FUEL", 100);
        power.addResource("POWER", 200);

        colony.addFacility(habitat);
        colony.addFacility(lab);
        colony.addFacility(power);
        colony.addFacility(comms);
        return colony;
    }

    private static ExplorationRover newRover() {
        ExplorationRover rover = new ExplorationRover("Rover-1", 101, "Habitat A", 3);
        rover.loadEquipment(new Equipment(1, "Spectrometer", EquipmentType.SCIENTIFIC,
                EquipmentCondition.WORKING, "Rover-1"));
        return rover;
    }

    private static Exploration newExploration(int id, double fuelNeeded) {
        Resources request = new Resources();
        request.AddRequirement("FUEL", fuelNeeded);
        List<EquipmentType> needs = new ArrayList<>();
        needs.add(EquipmentType.SCIENTIFIC);
        // (id, name, duration, resources, targets, minBattery, region, equipment)
        return new Exploration(id, "Explore Region Alpha", 3, request, null, 15,
                "Region Alpha", needs);
    }

    private static void printMission(Mission m) {
        System.out.println("Mission '" + m.getName() + "' | State: " + m.getState()
                + " | Progress: " + m.getProgress() + "/" + m.getDuration()
                + (m.getFailureReason() != null ? " | Reason: " + m.getFailureReason() : ""));
    }

    // =====================================================
    // Scenario 1: Successful Exploration
    // =====================================================

    private static void scenario1SuccessfulExploration() {
        title("SCENARIO 1: Successful Exploration");
        Colony colony = buildColony();
        Simulation sim = new Simulation(colony);

        Scientist scientist = new Scientist(skills("EXPLORATION"), 10, 100,
                "Dr. Amal", 1, true, "Habitat A");
        ExplorationRover rover = newRover();
        colony.addAgent(scientist);
        colony.addAgent(rover);

        Exploration mission = newExploration(1, 10);

        System.out.println("--- BEFORE EXECUTION ---");
        System.out.println(colony.getStatusReport());

        List<Agent> team = new ArrayList<>();
        team.add(scientist);
        team.add(rover);

        boolean accepted = colony.submitMission(mission, team);
        System.out.println("Accepted: " + accepted);
        printMission(mission);

        // the colony starts the mission on the first time unit
        for (int t = 1; t <= 3; t++) {
            sim.advanceTime(1);
            System.out.print("Time unit " + t + ": ");
            printMission(mission);
        }

        System.out.println("--- AFTER EXECUTION ---");
        System.out.println(colony.getStatusReport());
    }

    // =====================================================
    // Scenario 2: Mission Rejection (unsuitable agent)
    // =====================================================

    private static void scenario2MissionRejection() {
        title("SCENARIO 2: Mission Rejection (unsuitable agent)");
        Colony colony = buildColony();

        // skill is "Geology", not "EXPLORATION", so canPerform() is false
        Scientist unsuitable = new Scientist(skills("Geology"), 10, 100,
                "Dr. Omar", 2, true, "Habitat A");
        ExplorationRover rover = newRover();
        colony.addAgent(unsuitable);
        colony.addAgent(rover);

        Exploration mission = newExploration(2, 10);

        List<Agent> team = new ArrayList<>();
        team.add(unsuitable);
        team.add(rover);

        boolean accepted = colony.submitMission(mission, team);

        System.out.println("Accepted: " + accepted);
        System.out.println("Mission cannot be assigned.");
        System.out.println("Reason: " + mission.getFailureReason());
        printMission(mission);
        System.out.println("Mission active?        " + mission.isActive());          // false
        System.out.println("Colony active mission: " + colony.getActiveMission());   // null
        System.out.println("Agents still available? " + unsuitable.isAvailable()
                + ", " + rover.isAvailable());                                       // true, true
    }

    // =====================================================
    // Scenario 3: Resource Shortage
    // =====================================================

    private static void scenario3ResourceShortage() {
        title("SCENARIO 3: Resource Shortage");
        Colony colony = buildColony();

        Scientist scientist = new Scientist(skills("EXPLORATION"), 10, 100,
                "Dr. Amal", 1, true, "Habitat A");
        ExplorationRover rover = newRover();
        colony.addAgent(scientist);
        colony.addAgent(rover);

        // the colony holds 100 fuel, the mission asks for 500
        Exploration mission = newExploration(3, 500);

        System.out.println("Fuel before: " + colony.getColonyTotal("FUEL"));

        List<Agent> team = new ArrayList<>();
        team.add(scientist);
        team.add(rover);
        boolean accepted = colony.submitMission(mission, team);

        System.out.println("Accepted: " + accepted);
        printMission(mission);
        System.out.println("Fuel after:  " + colony.getColonyTotal("FUEL"));   // still 100
        System.out.println("Agents still available? " + scientist.isAvailable()
                + ", " + rover.isAvailable());
        System.out.println("Mission active? " + mission.isActive());
    }

    // =====================================================
    // Scenario 4: Emergency Handling (oxygen leak)
    // =====================================================

    private static void scenario4EmergencyHandling() {
        title("SCENARIO 4: Emergency Handling (oxygen leak)");
        runOxygenLeak("4a: responders available (leak repaired)", true);
        runOxygenLeak("4b: robot unavailable (leak unresolved)", false);
    }

    private static void runOxygenLeak(String label, boolean robotAvailable) {
        System.out.println();
        System.out.println("--- " + label + " ---");
        Colony colony = buildColony();

        Engineer engineer = new Engineer(skills("REPAIR"), 10, 100,
                "Eng. Sami", 3, true, "Laboratory");
        RepairRobot robot = new RepairRobot("Fixer-1", 102, "Power Station");
        colony.addAgent(engineer);
        colony.addAgent(robot);
        if (!robotAvailable) {
            robot.setAvailability(false);   // e.g. busy elsewhere
        }

        Habitat habitat = (Habitat) colony.findFacility("H1");
        System.out.println("Habitat oxygen before:       " + habitat.getOxygen());
        System.out.println("Colony spare parts before:   " + colony.getColonyTotal("SPARE_PARTS"));
        System.out.println("Robot battery before:        " + robot.getBatteryLevel());

        Emergency leak = new Emergency(1, EmergencyType.OXYGEN_LEAK, habitat, 50);
        colony.handleEmergency(leak);

        System.out.println(leak.getStatusSummary());
        System.out.println("Habitat oxygen after:        " + habitat.getOxygen());
        System.out.println("Habitat status:              " + habitat.getStatus());
        System.out.println("Colony spare parts after:    " + colony.getColonyTotal("SPARE_PARTS"));
        System.out.println("Robot battery after:         " + robot.getBatteryLevel());
        System.out.println("Engineer location/available: " + engineer.getCurrentLocation()
                + " / " + engineer.isAvailable());
        // 4a expected: RESOLVED, oxygen 250, spare parts 8, battery 95
        // 4b expected: UNRESOLVED with a reason, oxygen 200, spare parts 10, battery 100
    }

    // =====================================================
    // Scenario 5: Environmental Effect (dust storm)
    // =====================================================

    private static void scenario5EnvironmentalEffect() {
        title("SCENARIO 5: Environmental Effect (dust storm)");

        // 5a: a dust storm prevents the mission from starting
        System.out.println("--- 5a: storm before submission ---");
        Colony colony = buildColony();
        Scientist scientist = new Scientist(skills("EXPLORATION"), 10, 100,
                "Dr. Amal", 1, true, "Habitat A");
        ExplorationRover rover = newRover();
        colony.addAgent(scientist);
        colony.addAgent(rover);

        colony.getEnvironment().setCondition(Environment.EnvironmentCondition.DUST_STORM);
        System.out.println("Environment: " + colony.getEnvironment().getDescription());

        Exploration blocked = newExploration(5, 10);
        List<Agent> team = new ArrayList<>();
        team.add(scientist);
        team.add(rover);
        colony.submitMission(blocked, team);

        System.out.println("Exploration Mission cannot start.");
        System.out.println("Reason: " + blocked.getFailureReason());
        printMission(blocked);

        // 5b: a storm hits a mission that is already running
        System.out.println();
        System.out.println("--- 5b: storm during an active mission ---");
        Colony colony2 = buildColony();
        Simulation sim2 = new Simulation(colony2);
        Scientist scientist2 = new Scientist(skills("EXPLORATION"), 10, 100,
                "Dr. Amal", 11, true, "Habitat A");
        ExplorationRover rover2 = newRover();
        colony2.addAgent(scientist2);
        colony2.addAgent(rover2);

        Exploration running = newExploration(6, 10);
        List<Agent> team2 = new ArrayList<>();
        team2.add(scientist2);
        team2.add(rover2);
        colony2.submitMission(running, team2);
        sim2.advanceTime(1);                 // mission starts and progresses
        printMission(running);

        colony2.getEnvironment().setCondition(Environment.EnvironmentCondition.DUST_STORM);
        System.out.println("A dust storm begins.");
        sim2.advanceTime(1);
        printMission(running);               // FAILED, reason mentions the dust storm
        System.out.println("Agents released? " + scientist2.isAvailable()
                + ", " + rover2.isAvailable());
    }

    // =====================================================
    // Scenario 6 (extra): Research mission
    // =====================================================

    private static Research newResearch(int id, Facility lab) {
        Resources request = new Resources();
        request.AddRequirement("WATER", 5);
        List<Facility> targets = new ArrayList<>();
        targets.add(lab);
        List<String> labEquipment = new ArrayList<>();
        labEquipment.add("Microscope");
        // (id, name, duration, resources, targets, experimentName, labEquipment)
        return new Research(id, "Cell Study", 2, request, targets,
                "Cell Growth Experiment", labEquipment);
    }

    private static void scenario6Research() {
        title("SCENARIO 6 (extra): Research Mission");

        // 6a: everything available, the research completes
        System.out.println("--- 6a: successful research ---");
        Colony colony = buildColony();
        Simulation sim = new Simulation(colony);
        Laboratory lab = (Laboratory) colony.findFacility("L1");
        lab.addEquipment("Microscope");

        Scientist scientist = new Scientist(skills("RESEARCH"), 10, 100,
                "Dr. Layla", 20, true, "Laboratory");
        colony.addAgent(scientist);

        Research research = newResearch(7, lab);
        System.out.println("Water before: " + colony.getColonyTotal("WATER"));

        List<Agent> team = new ArrayList<>();
        team.add(scientist);
        boolean accepted = colony.submitMission(research, team);
        System.out.println("Accepted: " + accepted);
        printMission(research);

        for (int t = 1; t <= 2; t++) {
            sim.advanceTime(1);
            System.out.print("Time unit " + t + ": ");
            printMission(research);
        }
        System.out.println("Water after:  " + colony.getColonyTotal("WATER"));   // 95
        System.out.println("Scientist available again? " + scientist.isAvailable());

        // 6b: the laboratory is on fire, so the research cannot start
        System.out.println();
        System.out.println("--- 6b: laboratory on fire ---");
        Colony colony2 = buildColony();
        Laboratory lab2 = (Laboratory) colony2.findFacility("L1");
        lab2.addEquipment("Microscope");
        Scientist scientist2 = new Scientist(skills("RESEARCH"), 10, 100,
                "Dr. Layla", 21, true, "Laboratory");
        colony2.addAgent(scientist2);

        lab2.catchFire();   // laboratory unavailable, equipment damaged

        Research blocked = newResearch(8, lab2);
        List<Agent> team2 = new ArrayList<>();
        team2.add(scientist2);
        colony2.submitMission(blocked, team2);

        System.out.println("Research Mission cannot start.");
        System.out.println("Reason: " + blocked.getFailureReason());
        printMission(blocked);
        System.out.println("Scientist still available? " + scientist2.isAvailable());   // true
    }

    // =====================================================
    // Extra: Resources class tests
    // =====================================================

    private static void runResourceTests() {
        title("EXTRA: RESOURCES CLASS TESTS");
        Resources resources = new Resources();

        System.out.println("\n--- TEST 1: DISPLAY RESOURCES ---");
        resources.DisplayResources();

        System.out.println("\n--- TEST 2: ADD RESOURCES ---");
        resources.AddResources("OXYGEN", 100);
        resources.AddResources("FOOD", 200);
        resources.AddResources("WATER", 150);
        resources.AddResources("FUEL", 80);
        resources.AddResources("POWER", 120);
        resources.AddResources("MEDICAL_SUPPLIES", 50);
        resources.AddResources("SPARE_PARTS", 40);
        resources.DisplayResources();
        resources.AddResources("WOOD", 20);   // invalid resource type

        System.out.println("\n--- TEST 3: REMOVE RESOURCES ---");
        resources.RemoveResources("OXYGEN", 10);
        resources.RemoveResources("FOOD", 500);   // insufficient
        resources.RemoveResources("WOOD", 10);    // invalid

        System.out.println("\n--- TEST 4: CHECK RESOURCE AVAILABILITY ---");
        System.out.println("Oxygen check result: " + resources.HasAtLeast("OXYGEN", 50));
        System.out.println("Food check result: " + resources.HasAtLeast("FOOD", 500));
        System.out.println("Invalid resource check result: " + resources.HasAtLeast("WOOD", 10));

        System.out.println("\n--- TEST 5: ADD REQUIREMENTS (too much oxygen) ---");
        resources.AddRequirement("OXYGEN", 1000);
        resources.AddRequirement("FOOD", 30);
        resources.AddRequirement("WATER", 40);
        resources.AddRequirement("FUEL", 10);
        resources.AddRequirement("POWER", 15);
        resources.AddRequirement("MEDICAL_SUPPLIES", 5);
        resources.AddRequirement("SPARE_PARTS", 8);
        resources.AddRequirement("WOOD", 10);   // invalid

        System.out.println("\n--- TEST 6: VIEW REQUIREMENTS ---");
        resources.ViewRequirments();

        System.out.println("\n--- TEST 7: INSUFFICIENT RESOURCE TEST ---");
        System.out.println("Expected: transaction rejected because oxygen is insufficient.");
        resources.GetRequirments();

        System.out.println("\n--- TEST 8: VERIFY NOTHING WAS DEDUCTED ---");
        resources.DisplayResources();

        System.out.println("\n--- TEST 9: SUCCESSFUL TRANSACTION ---");
        resources.ClearRequirements();
        resources.AddRequirement("OXYGEN", 20);
        resources.AddRequirement("FOOD", 30);
        resources.AddRequirement("WATER", 40);
        resources.AddRequirement("FUEL", 10);
        resources.AddRequirement("POWER", 15);
        resources.AddRequirement("MEDICAL_SUPPLIES", 5);
        resources.AddRequirement("SPARE_PARTS", 8);
        resources.ViewRequirments();
        resources.GetRequirments();
        resources.DisplayResources();
    }

    // =====================================================
    // Extra: CrewMember tests
    // =====================================================

    private static void runCrewTests() {
        title("EXTRA: CREW MEMBER TESTS");
        Scientist s = new Scientist(skills("Geology"), 10, 100, "Sara", 1, true, "Lab A");
        s.display();

        s.damage(80);
        System.out.println("After damage(80): health = " + s.getHealthStatus()
                + ", usable = " + s.isUsable());

        s.heal(500);
        System.out.println("After heal(500): health = " + s.getHealthStatus());

        s.increaseFatigue(90);
        System.out.println("After increaseFatigue(90): fatigue = " + s.getFatigueLevel()
                + ", usable = " + s.isUsable());

        s.setAvailability(false);
        System.out.println("Unavailable: ready = " + s.isReady());
    }
}