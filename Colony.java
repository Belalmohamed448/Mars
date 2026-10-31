package lab5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Colony {

    private static final double RESPONSE_SPARE_PARTS = 2;   // spec example
    private static final int RESPONSE_BATTERY = 5;          // spec example

    private final ArrayList<Agent> agents;
    private final ArrayList<Facility> facilities;
    private final ArrayList<Mission> missionHistory;
    private final ArrayList<Emergency> emergencyHistory;

    private Environment environment;
    private Mission activeMission;

    public Colony() {
        this.agents = new ArrayList<>();
        this.facilities = new ArrayList<>();
        this.missionHistory = new ArrayList<>();
        this.emergencyHistory = new ArrayList<>();
        this.environment = new Environment();
        this.activeMission = null;
    }

    // ---------- agents ----------

    public boolean addAgent(Agent agent) {
        if (agent == null) {
            return false;
        }
        for (Agent a : agents) {
            if (agent.getId() == a.getId()) {
                return false;
            }
        }
        agents.add(agent);
        return true;
    }

    public Agent findAgent(int id) {
        for (Agent a : agents) {
            if (id == a.getId()) {
                return a;
            }
        }
        return null;
    }

    public List<Agent> getAgents() {
        return Collections.unmodifiableList(agents);
    }

    // ---------- facilities ----------

    public boolean addFacility(Facility facility) {
        if (facility == null) {
            return false;
        }
        for (Facility f : facilities) {
            if (facility.getId().equals(f.getId())) {
                return false;
            }
        }
        facilities.add(facility);
        return true;
    }

    public Facility findFacility(String id) {
        for (Facility f : facilities) {
            if (id != null && id.equals(f.getId())) {
                return f;
            }
        }
        return null;
    }

    public List<Facility> getFacilities() {
        return Collections.unmodifiableList(facilities);
    }

    // ---------- environment / state ----------

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException("Environment cannot be null.");
        }
        this.environment = environment;
    }

    public Mission getActiveMission() {
        return activeMission;
    }

    public List<Mission> getMissionHistory() {
        return Collections.unmodifiableList(missionHistory);
    }

    public List<Emergency> getEmergencyHistory() {
        return Collections.unmodifiableList(emergencyHistory);
    }

    private boolean addMissionToHistory(Mission mission) {
        if (mission == null) {
            return false;
        }
        missionHistory.add(mission);
        return true;
    }

    // ---------- resources (colony-level, spec section 5.1) ----------

    private double amountIn(Facility f, String type) {
        switch (type) {
            case "OXYGEN":
                return f.R1.getOxygen();
            case "WATER":
                return f.R1.getWater();
            case "FOOD":
                return f.R1.getFood();
            case "FUEL":
                return f.R1.getFuel();
            case "POWER":
                return f.R1.getPower();
            case "MEDICAL_SUPPLIES":
                return f.R1.getMedicalSupplies();
            case "SPARE_PARTS":
                return f.R1.getSpareParts();
            default:
                return 0;
        }
    }

    // sum over operational facilities only; destroyed/unavailable ones do not count
    public double getColonyTotal(String type) {
        double total = 0;
        for (Facility f : facilities) {
            if (f.isOperational()) {
                total += amountIn(f, type);
            }
        }
        return total;
    }

    // returns null if everything is available, otherwise the failure reason
    private String checkResources(Resources request) {
        for (Map.Entry<String, Double> entry : request.getRequestRec().entrySet()) {
            double available = getColonyTotal(entry.getKey());
            if (available < entry.getValue()) {
                return "Insufficient " + entry.getKey() + ": required "
                        + entry.getValue() + ", available " + available + ".";
            }
        }
        return null;
    }

    // only called after checkResources succeeded; draws facility by facility
    // in registration order (allocation policy: document this in the README)
    private void consumeResources(Resources request) {
        if (request == null || request.getRequestRec() == null) {
            return;
        }
        for (Map.Entry<String, Double> entry : request.getRequestRec().entrySet()) {
            String type = entry.getKey();
            double needed = entry.getValue();

            for (Facility f : facilities) {
                if (needed <= 0) {
                    break;
                }
                if (f.isOperational()) {
                    double take = Math.min(amountIn(f, type), needed);
                    if (take > 0) {
                        f.R1.RemoveResources(type, take);
                        needed -= take;
                    }
                }
            }
        }
    }

    // ---------- missions ----------

    public boolean submitMission(Mission mission, List<Agent> team) {
        if (mission == null) {
            return false;
        }

        if (mission.getState() != MissionStatus.PENDING) {
            return false;
        }

        addMissionToHistory(mission);

        if (activeMission != null && activeMission.isActive()) {
            mission.failMission("Another mission is currently active.");
            return false;
        }

        if (!mission.canStartIn(environment)) {
            mission.failMission(mission.getStartBlockedReason());
            return false;
        }

        if (team != null) {
            for (Agent agent : team) {
                if (agent == null) {
                    mission.failMission("A required agent is missing.");
                    return false;
                }
                if (findAgent(agent.getId()) == null) {
                    mission.failMission("Agent with ID " + agent.getId()
                            + " is not registered in this colony.");
                    return false;
                }
                if (!agent.canOperateIn(environment)) {
                    mission.failMission("Agent " + agent.getName()
                            + " cannot operate under the current environmental conditions.");
                    return false;
                }
            }
        }

        if (mission.getResourceRequest() != null) {
            String shortage = checkResources(mission.getResourceRequest());
            if (shortage != null) {
                mission.failMission(shortage);
                return false;
            }
        }

        // checks capability and availability of every agent before assigning any
        if (!mission.assignAgents(team)) {
            return false;
        }

        // consume only after everything has been accepted (all-or-nothing)
        if (mission.getResourceRequest() != null) {
            consumeResources(mission.getResourceRequest());
        }

        activeMission = mission;
        return true;
    }

    // ---------- emergencies (spec section 9) ----------

    public boolean handleEmergency(Emergency e) {
        if (e == null || !e.isActive()) {
            return false;
        }
        emergencyHistory.add(e);

        if (e.getType() != EmergencyType.OXYGEN_LEAK
                || !(e.getAffectedFacility() instanceof Habitat)) {
            e.markUnresolved("No response procedure implemented for " + e.getType() + ".");
            return false;
        }

        Habitat habitat = (Habitat) e.getAffectedFacility();
        habitat.applyOxygenLeak(e.getOxygenLoss());          // initial loss (spec 9.2)

        // ask each agent whether it can handle the emergency; one human, one robot
        CrewMember human = null;
        Robot robot = null;
        for (Agent a : agents) {
            if (!a.isReady() || !a.canHandle(e)) {
                continue;
            }
            if (a instanceof Robot) {
                if (robot == null) {
                    robot = (Robot) a;
                }
            } else if (a instanceof CrewMember) {
                if (human == null) {
                    human = (CrewMember) a;
                }
            }
        }

        // check everything BEFORE consuming anything
        String problem = null;
        if (human == null) {
            problem = "No available human responder able to handle " + e.getType() + ".";
        } else if (robot == null) {
            problem = "No available robotic responder able to handle " + e.getType() + ".";
        } else if (!robot.hasEnoughBattery(RESPONSE_BATTERY)) {
            problem = "Robot " + robot.getName() + " has insufficient battery.";
        } else if (getColonyTotal("SPARE_PARTS") < RESPONSE_SPARE_PARTS) {
            problem = "Insufficient spare parts: required " + RESPONSE_SPARE_PARTS
                    + ", available " + getColonyTotal("SPARE_PARTS") + ".";
        }
        if (problem != null) {
            return failResponse(e, habitat, problem);
        }

        List<Agent> team = new ArrayList<>();
        team.add(human);
        team.add(robot);
        if (!e.dispatch(team)) {
            return failResponse(e, habitat, "Dispatch failed.");
        }

        robot.drainBattery(RESPONSE_BATTERY);
        Resources cost = new Resources();
        cost.AddRequirement("SPARE_PARTS", RESPONSE_SPARE_PARTS);
        consumeResources(cost);

        habitat.sealLeak();
        e.resolve();                                         // also releases responders
        return true;
    }

    // unresolved leak: the same oxygen amount is deducted a second time (spec 9.2)
    private boolean failResponse(Emergency e, Habitat habitat, String reason) {
        habitat.applyOxygenLeak(e.getOxygenLoss());
        e.markUnresolved(reason);
        return false;
    }

    // ---------- simulation ----------

    public void advanceTime() {
        for (Agent agent : agents) {
            agent.update();
        }

        if (activeMission == null || !activeMission.isActive()) {
            activeMission = null;
            return;
        }

        // REPAIRED PART: ASSIGNED -> IN_PROGRESS, then every assigned agent
        // reacts in its own way (the rover moves to the surface)
        if (activeMission.getState() == MissionStatus.ASSIGNED) {
            activeMission.startMission();
            for (Agent a : activeMission.getAssignedAgents()) {
                a.onMissionStart(activeMission);
            }
        }

        // environment and agent checks: each agent decides for itself (polymorphism)
        for (Agent agent : activeMission.getAssignedAgents()) {
            if (!agent.canOperateIn(environment)) {
                activeMission.failMission(environmentFailureReason(agent));
                break;
            }
            if (!agent.isUsable()) {
                activeMission.failMission("Agent " + agent.getName()
                        + " is no longer usable (low battery, damage, or exhaustion).");
                break;
            }
        }

        if (activeMission.isActive()) {
            activeMission.advanceTime();
        }

        if (activeMission.isFinished()) {
            activeMission = null;
        }
    }

    private String environmentFailureReason(Agent agent) {
        if (environment.isDustStorm()) {
            return "Surface operations are not allowed during a dust storm.";
        }
        if (environment.isCommunicationDisrupted()) {
            return "Communication disruption aborted the mission (" + agent.getName() + ").";
        }
        return "Agent " + agent.getName() + " cannot operate in the current environment.";
    }

    // ---------- report ----------

    public String getStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COLONY STATUS REPORT ===\n");
        sb.append("Environment: ").append(environment.getDescription()).append("\n\n");

        sb.append("--- Colony-Wide Resource Totals ---\n");
        String[] resourceTypes = {"OXYGEN", "WATER", "FOOD", "FUEL", "POWER",
            "MEDICAL_SUPPLIES", "SPARE_PARTS"};
        for (String type : resourceTypes) {
            sb.append(type).append(": ").append(getColonyTotal(type)).append("\n");
        }
        sb.append("\n");

        sb.append("--- Agents ---\n");
        if (agents.isEmpty()) {
            sb.append("No agents registered.\n");
        } else {
            for (Agent agent : agents) {
                sb.append(agent.getStatusSummary()).append("\n");
            }
        }
        sb.append("\n");

        sb.append("--- Facilities ---\n");
        if (facilities.isEmpty()) {
            sb.append("No facilities registered.\n");
        } else {
            for (Facility facility : facilities) {
                sb.append("Facility ID: ").append(facility.getId())
                        .append(" | Name: ").append(facility.getName())
                        .append(" | Status: ").append(facility.getStatus()).append("\n");
            }
        }
        sb.append("\n");

        sb.append("--- Active Mission ---\n");
        if (activeMission != null) {
            sb.append("Mission ID: ").append(activeMission.getId())
                    .append(" | Name: ").append(activeMission.getName())
                    .append(" | State: ").append(activeMission.getState())
                    .append(" | Progress: ").append(activeMission.getProgress())
                    .append("/").append(activeMission.getDuration()).append("\n");
        } else {
            sb.append("No active mission.\n");
        }
        sb.append("\n");

        sb.append("--- Mission History & Outcomes ---\n");
        if (missionHistory.isEmpty()) {
            sb.append("No missions in history.\n");
        } else {
            for (Mission mission : missionHistory) {
                sb.append("Mission ID: ").append(mission.getId())
                        .append(" | Name: ").append(mission.getName())
                        .append(" | State: ").append(mission.getState());
                if (mission.getFailureReason() != null) {
                    sb.append(" | Reason: ").append(mission.getFailureReason());
                }
                sb.append("\n");
            }
        }
        sb.append("\n");

        sb.append("--- Emergency History & Outcomes ---\n");
        if (emergencyHistory.isEmpty()) {
            sb.append("No emergencies recorded.\n");
        } else {
            for (Emergency em : emergencyHistory) {
                sb.append(em.getStatusSummary()).append("\n");
            }
        }

        return sb.toString();
    }
}