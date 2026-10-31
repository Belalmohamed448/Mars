MARS-OPS: Autonomous Mars Colony Management & Emergency Simulator

Alexandria University, Faculty of Engineering

Computer and Communication Engineering | Programming II | Fall 2026, Lab 5

Team Members

Belal Mohamed Ahmed – 9852

Mohamed Ahmed Elsayed – 9986

Youssef Khalid Galal – 10090

Youssef Aly Mahmoud – 10094

1. System Description

MARS-OPS is a Java simulation of a small research colony on Mars. The colony manages its people, robots, facilities, and resources, accepts missions, advances simulated time, reacts to environmental conditions, and responds to emergencies. It can print a colony status report at any moment.

The design goal is that objects collaborate: no single class holds all the logic. The colony coordinates, but each object knows its own rules.

Main Components (package lab5)

Agents: Agent (abstract), CrewMember (abstract), Scientist, Engineer, MedicalOfficer, Robot (abstract), ExplorationRover, RepairRobot, Drone — Anything that can perform missions or respond to emergencies. Each agent answers canPerform(Mission) and canHandle(Emergency) itself.

Facilities: Facility (abstract), Habitat, Laboratory, PowerStation, CommunicationStation — Hold resource stock and have an operational status.

Resources: Resources — Quantities of oxygen, water, food, fuel, power, medical supplies, and spare parts. Also used as a mission's resource request.

Equipment: Equipment, EquipmentType, EquipmentCondition — Equipment carried by rovers, with a condition (working, damaged, destroyed).

Missions: Mission (abstract), Exploration, RepairMission, RescueMission, MissionType, MissionStatus — Mission data and the mission lifecycle.

Emergencies: Emergency, EmergencyType, EmergencyStatus — One emergency event, its responders, and its outcome.

Environment: Environment (with EnvironmentCondition) — Current Mars condition: normal, dust storm, communication disruption.

Coordination: Colony, Simulation — Colony owns everything and enforces colony-level rules. Simulation advances time.

Testing: main — Runs the five required demonstration scenarios plus extra checks.

2. Design Decisions and Assumptions

2.1 The Four OOP Concepts

Encapsulation: Fields are private (or protected where subclasses need them). Lists are copied on the way in and returned as unmodifiable views (for example, Mission.getAssignedAgents(), CrewMember.getSkills()). Values that must stay in range (health, fatigue, battery) are clamped inside the object.

Abstraction: Agent, CrewMember, Robot, Facility, and Mission are abstract and declare what every subclass must provide.

Inheritance: Scientist/Engineer/MedicalOfficer extend CrewMember; ExplorationRover/RepairRobot/Drone extend Robot; both extend Agent. The facility and mission hierarchies work the same way.

Polymorphism: The colony and missions work with Agent and Mission references. canPerform, canHandle, canOperateIn, canStartIn, update, and getStatusSummary behave differently per subclass.

2.2 Capability Requirements (Spec 6.1)

A mission never checks the concrete type of an agent. It asks agent.canPerform(mission), and emergencies ask agent.canHandle(emergency).

CrewMember.canPerform is true when one of the person's skills matches the mission type name (for example, the skill "EXPLORATION" for an EXPLORATION mission).

Robot subclasses decide by mission type, usable state, battery and, for the rover, carried equipment.

ExplorationRover and Drone accept exploration and rescue; RepairRobot accepts repair only.

One deliberate exception: Exploration.execute looks for the assigned ExplorationRover with instanceof, because that method is specific to exploration.

2.3 Available Versus Usable

Available: The agent is not currently committed to a mission or an emergency (isAvailable()).

Usable: The agent is in working condition (isUsable()).

Ready: Both (isReady()).

Usability thresholds (defined as constants):

Crew Member: Usable when health > 30 and fatigue < 80 (both on a 0–100 scale).

Robot: Usable when health status is OPERATIONAL and battery > 0.

(A robot also needs $\text{minBattery} + 10$ battery to perform a mission; 10 is a reserve that must remain after the task).

2.4 Time Effects on Agents

Each time unit, update() is called on every agent:

Crew: Fatigue $+2$ if busy, $-1$ if idle.

Robots: Battery drains by the robot's own rate per unit while busy (rover: 5, repair robot: 3, drone: 4) and recharges $+2$ per unit while idle.

2.5 Object Relationships (Spec 11.1)

Inheritance: Agent, facility, and mission hierarchies ("is-a" relationships).

Composition: Facility $\rightarrow$ Resources ($R1$ created in the constructor); Colony $\rightarrow$ Environment. The part is created by the owner and has no meaning without it.

Aggregation: Colony $\rightarrow$ agents, facilities, missions, emergencies; ExplorationRover $\rightarrow$ Equipment. The parts exist independently and can be moved or reused.

Association: Mission $\rightarrow$ Agent, Facility, Resources; Emergency $\rightarrow$ Facility, Agent; Simulation $\rightarrow$ Colony; RescueMission $\rightarrow$ Agent. Objects that use or refer to each other.

2.6 Other Decisions Left Open by the Specification

A new mission starts in PENDING (not yet accepted or rejected). A rejected mission goes PENDING $\rightarrow$ FAILED and never becomes ASSIGNED or active.

Agents are assigned all-or-nothing. Mission.assignAgents checks every agent first, and only if all pass does it mark them busy. A rejection never leaves some agents stuck busy.

Only a PENDING mission can be submitted. Resubmitting a running or finished mission does nothing; a failed mission is never retried (a new mission object must be created).

Agent availability is checked colony-wide. Every agent must be registered in the colony, and the colony's one-active-mission rule is enforced by Colony, not by the mission.

Agent IDs are unique across humans and robots (they share one list in the colony).

Facility status is a String (OPERATIONAL, UNAVAILABLE, DESTROYED), and only operational facilities count toward resource totals.

3. Missions

3.1 Mission Types and Requirements

Type

Class

Starts When

Notes

Exploration

Exploration

Not during a dust storm

Needs a rover with required equipment types and enough battery.

Repair

RepairMission

Always (indoor work)

Targets exactly one facility.

Rescue

RescueMission

Not during communication disruption

Refers to the stranded agent.

3.2 Mission States (Specs 7, 11.4)

PENDING --assignAgents(), all agents ready and capable--> ASSIGNED
PENDING --any rejection---------------------------------> FAILED
ASSIGNED --startMission()-----------------------------> IN_PROGRESS
ASSIGNED --start check fails---------------------------> FAILED
IN_PROGRESS --advanceTime(), progress < duration------> IN_PROGRESS
IN_PROGRESS --advanceTime(), progress >= duration-----> COMPLETED
IN_PROGRESS --failure (dust storm, equipment, battery)-> FAILED


A mission starts counting its duration when it becomes IN_PROGRESS. Each time unit adds 1 to its progress.

Only startMission, advanceTime, complete, failMission, and assignAgents change a mission's state, and each refuses an illegal move (for example, complete() on a PENDING mission does nothing).

Agents are released on both completion and failure. A mission that fails from PENDING never marked its agents busy, so it releases nobody.

Every rejected or failed mission stores a failure reason (getFailureReason()).

3.3 Rejection Reasons

A submitted mission becomes FAILED immediately with a clear reason if:

Another mission is active.

The environment blocks it (e.g., a dust storm for exploration).

An agent is not registered in the colony.

Required resources are insufficient.

An agent is missing, not ready, or lacks the required capability.

3.4 One Active Mission at a Time (Spec 6.2)

The colony keeps one activeMission. Emergency responses are not subject to this limit, but responders must be available (not part of the active mission).

4. Resource Rules (Spec 5)

Resources are stored in facilities (Facility.R1) and managed at the colony level.

Colony-wide total = sum over all operational facilities. Stock in a destroyed or unavailable facility does not count.

A request may be supplied by several facilities (e.g., 300 oxygen in one and 200 in another can satisfy a 450 request, but not 600).

All-or-nothing: The colony checks the whole request before touching anything. If any single resource is short, nothing is consumed, the operation fails with a reason (e.g., Insufficient FUEL: required 500.0, available 100.0), and the failed mission never takes any agent.

Quantities never go negative: removal refuses to exceed stock, and an oxygen leak is clamped at zero.

The colony status report shows colony-wide totals.

Consumption timing: A mission's resources are consumed when the colony accepts the mission.

Allocation policy: Facilities supply in the order they were added to the colony.

Resources already consumed by an accepted mission are not refunded if the mission later fails.

5. Emergency Rules (Spec 9)

5.1 General Rules

An emergency has a type, an affected location (facility or Martian surface), and a status: ACTIVE, RESOLVED, or UNRESOLVED.

A responder is appropriate if it reports canHandle(emergencyType). Concrete types are never checked.

A responder must be available (not on the active mission) and usable.

Dispatching moves responders to the affected facility.

Emergencies do not interrupt the active mission.

If required responders are unavailable: the emergency is marked UNRESOLVED, a clear reason is recorded, no response resources are consumed, and it is not retried automatically.

Responders are released when the response ends.

5.2 Required Responders per Type

Emergency

Location

Required Responders

Oxygen leak

A habitat

At least 1 human and 1 robot able to repair (Engineer and RepairRobot)

Fire

A laboratory

1 human and 1 robot

Rover breakdown

Martian surface

1 robot

Medical emergency

A facility

1 human

5.3 Oxygen Leak (Spec 9.2)

Triggered manually with Colony.handleEmergency(...).

Affected habitat is identified.

Loss amount (e.g., 50) is deducted from that habitat only, lowering the colony total (one-time deduction).

Responders are chosen and checked for availability.

Response resources checked: repair uses 2 spare parts and 5 battery units on the repair robot.

If available: responders dispatched, leak repaired, resources consumed, emergency RESOLVED.

If not available: same amount deducted again (unresolved leak costs twice the oxygen), emergency UNRESOLVED with a reason, nothing consumed.

Habitat oxygen never falls below zero.

A leaking habitat stays OPERATIONAL, so its other stock (water, food) still counts toward colony totals.

5.4 Current Limitations for Other Emergencies

Only Engineer and RepairRobot report that they can handle an emergency (OXYGEN_LEAK). Scientist, MedicalOfficer, ExplorationRover, and Drone report none. Thus, fire, rover breakdown, and medical emergencies have their types, requirements, and effects defined (Laboratory.catchFire(), ExplorationRover.breakDown()), but no responder yet reports that it can handle them, so those emergencies would end UNRESOLVED.

6. Environmental Effects (Spec 8)

Condition

Effects

Normal

No restriction.

Dust storm

Exploration missions cannot start. Rovers cannot operate. An active exploration (rover) mission fails.

Communication disruption

Rescue missions cannot start. Remotely controlled drones cannot be controlled.

Repair missions are indoor work and are not blocked by either condition.

Running missions: If a new condition is incompatible with an active mission (e.g., a dust storm during exploration), the mission moves to FAILED with a reason naming the condition, and its agents are released. Consumed resources are not refunded. Unaffected missions continue normally.

7. UML Design (Spec 11)

All five diagram types are in MARS-OPS-UML.drawio (open with draw.io). Each diagram is on a separate page:

Page 1: Class diagram

Page 2: Object diagram (Scenario 1 immediately after the colony accepts the mission)

Page 3: State machine diagram (Mission lifecycle)

Page 4: Sequence diagram A: Mission submission and execution

Page 5: Sequence diagram B: Oxygen leak emergency

Page 6: Sequence diagram C: Environmental restriction (dust storm)

Page 7: Use case diagram

Exported images and PDFs of these diagrams are located in the uml/ folder.

8. Compiling and Running

Requirements: JDK 9 or later (uses List.of and Map.of).

NetBeans: Open the project, then go to Run $\rightarrow$ Run Project (or press F6). The main class is lab5.main.

Command Line (from the parent folder containing the lab5 source directory):

javac -d out lab5/*.java
java -cp out lab5.main


9. Running the Demonstration Scenarios (Spec 12)

Running lab5.main executes all five required scenarios in order, followed by extra unit-style checks. Each scenario builds a fresh colony so scenarios never affect each other.

Scenario

Method in main

Expected Result

1. Successful exploration

successfulExploration()

Scientist and rover accepted. Mission goes ASSIGNED $\rightarrow$ IN_PROGRESS $\rightarrow$ COMPLETED after 3 time units. Status report printed before and after. Agents are available again, rover battery has dropped.

2. Mission rejection

missionRejection()

Scientist lacks the EXPLORATION skill. Mission is FAILED immediately with "does not possess the required capability" and never becomes active.

3. Resource shortage

resourceShortage()

Mission asks for 500 fuel while colony has 100. Rejected with Insufficient FUEL ..., fuel stays at 100, nothing goes negative.

4. Emergency handling

emergencyHandling()

4a: Responders available, leak repaired, oxygen $300 \rightarrow 250$, spare parts $10 \rightarrow 8$, robot battery $100 \rightarrow 95$, RESOLVED. 



4b: Robot unavailable, UNRESOLVED with a reason, oxygen $300 \rightarrow 200$ (deducted twice), nothing consumed.

5. Environmental effect

environmentalEffect()

5a: During a dust storm, exploration cannot start: "Surface operations are not allowed during a dust storm." 



5b: A storm during an active mission fails it and releases agents.

To run a single scenario, comment out the other calls at the top of main().

10. Testing

The main class (lab5.main) serves as the test driver required by the specification. It contains the five scenarios above along with two extra test groups:

runResourceTests(): Adding, removing, and checking resources, invalid types, insufficient requests, and a successful transaction.

runCrewTests(): Health and fatigue clamping, usability thresholds, and the isReady() rule.

11. Known Limitations

MissionType.RESEARCH exists, but there is no research mission class yet.

There is no dedicated storage facility class.

Laboratory keeps its equipment as a list of names, so facilities are not linked to the Equipment class. Only rovers carry Equipment objects.

Facility status is stored as a String.

Fire, rover breakdown, and medical emergencies are modeled but not yet answered by any responder (see Section 5.4).

The Resources class prints a message on every add or remove, which makes console output somewhat noisy.

12. Project Structure for Submission

StudentID-Prog-II-Lab5/
├── lab5/            (all .java source files)
├── uml/             (MARS-OPS-UML.drawio and exported PDF/PNG)
└── README.md

