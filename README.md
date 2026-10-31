MARS-OPS: Autonomous Mars Colony Management & Emergency Simulator
Alexandria University, Faculty of Engineering. Computer and Communication Engineering, Programming II, Fall 2026, Lab 5.

Team (group of 4): Belal Mohamed Ahmed – 9852, Mohamed Ahmed Elsayed – 9986, Youssef Khalid Galal – 10090, Youssef Aly Mahmoud – 10094

1. System description
MARS-OPS is a Java simulation of a small research colony on Mars. The colony manages its people, robots, facilities and resources, accepts missions, advances simulated time, reacts to environmental conditions, and responds to emergencies. It can print a colony status report at any moment.

The design goal is that objects collaborate: no single class holds all the logic. The colony coordinates, but each object knows its own rules.

Main components (package lab5)
Area	Classes	Responsibility
Agents	Agent (abstract), CrewMember (abstract), Scientist, Engineer, MedicalOfficer, Robot (abstract), ExplorationRover, RepairRobot, Drone	Anything that can perform missions or respond to emergencies. Each agent answers canPerform(Mission) and canHandle(Emergency) itself.
Facilities	Facility (abstract), Habitat, Laboratory, PowerStation, CommunicationStation	Hold resource stock and have an operational status.
Resources	Resources	Quantities of oxygen, water, food, fuel, power, medical supplies and spare parts. Also used as a mission's resource request.
Equipment	Equipment, EquipmentType, EquipmentCondition	Equipment carried by rovers, with a condition (working, damaged, destroyed).
Missions	Mission (abstract), Exploration, RepairMission, RescueMission, MissionType, MissionStatus	Mission data and the mission lifecycle.
Emergencies	Emergency, EmergencyType, EmergencyStatus	One emergency event, its responders and its outcome.
Environment	Environment (with EnvironmentCondition)	Current Mars condition: normal, dust storm, communication disruption.
Coordination	Colony, Simulation	Colony owns everything and enforces the colony-level rules. Simulation advances time.
Testing	main	Runs the five required demonstration scenarios plus extra checks.
2. Design decisions and assumptions
2.1 The four OOP concepts
Encapsulation. Fields are private (or protected where subclasses need them). Lists are copied on the way in and returned as unmodifiable views (for example Mission.getAssignedAgents(), CrewMember.getSkills()). Values that must stay in range (health, fatigue, battery) are clamped inside the object.
Abstraction. Agent, CrewMember, Robot, Facility and Mission are abstract and declare what every subclass must provide.
Inheritance. Scientist/Engineer/MedicalOfficer extend CrewMember; ExplorationRover/RepairRobot/Drone extend Robot; both extend Agent. The facility and mission hierarchies work the same way.
Polymorphism. The colony and the missions work with Agent and Mission references. canPerform, canHandle, canOperateIn, canStartIn, update and getStatusSummary behave differently per subclass.
2.2 Capability requirements (spec 6.1)
A mission never checks the concrete type of an agent. It asks agent.canPerform(mission), and emergencies ask agent.canHandle(emergency).

CrewMember.canPerform is true when one of the person's skills matches the mission type name (for example the skill "EXPLORATION" for an EXPLORATION mission).
Robot subclasses decide by mission type, usable state, battery and, for the rover, carried equipment.
ExplorationRover and Drone accept exploration and rescue; RepairRobot accepts repair only.
One deliberate exception: Exploration.execute looks for the assigned ExplorationRover with instanceof, because that method is specific to exploration.

2.3 Available versus usable
Available: the agent is not currently committed to a mission or an emergency (isAvailable()).
Usable: the agent is in working condition (isUsable()).
Ready: both (isReady()).
Usability thresholds, defined as constants:

Agent	Usable when
Crew member	health above 30 and fatigue below 80 (both on a 0–100 scale)
Robot	health status is OPERATIONAL and battery above 0
A robot also needs minBattery + 10 battery to perform a mission (10 is a reserve that must remain after the task).

2.4 Time effects on agents
Each time unit, update() is called on every agent:

Crew: fatigue +2 if busy, −1 if idle.
Robots: battery drains by the robot's own rate per unit while busy (rover 5, repair robot 3, drone 4) and recharges +2 per unit while idle.
2.5 Object relationships (spec 11.1)
Relationship	Where it is used	Reason
Inheritance	Agent, facility and mission hierarchies	"is-a" relationships
Composition	Facility ◆ Resources (R1 created in the constructor); Colony ◆ Environment	The part is created by the owner and has no meaning without it
Aggregation	Colony ◇ agents, facilities, missions, emergencies; ExplorationRover ◇ Equipment	The parts exist independently and can be moved or reused
Association	Mission → Agent, Facility, Resources; Emergency → Facility, Agent; Simulation → Colony; RescueMission → Agent	Objects that use or refer to each other
All of these appear in the class diagram and in the Java fields.

2.6 Other decisions left open by the specification
Mission starts in PENDING. A new mission has not yet been accepted or rejected. A rejected mission goes PENDING → FAILED and never becomes ASSIGNED or active.
Agents are assigned all-or-nothing. Mission.assignAgents checks every agent first, and only if all pass does it mark them busy. A rejection never leaves some agents stuck busy.
Only a PENDING mission can be submitted. Resubmitting a running or finished mission does nothing, so a failed mission is never retried (a new mission object must be created).
Agent availability is checked colony-wide. Every agent must be registered in the colony, and the colony's one-active-mission rule is enforced by Colony, not by the mission.
Agent ids are unique across humans and robots (they share one list in the colony).
Facility status is a String (OPERATIONAL, UNAVAILABLE, DESTROYED), and only operational facilities count toward resource totals.
3. Missions
3.1 Mission types and requirements
Type	Class	Starts when	Notes
Exploration	Exploration	Not during a dust storm	Needs a rover with the required equipment types and enough battery
Repair	RepairMission	Always (indoor work)	Targets exactly one facility
Rescue	RescueMission	Not during communication disruption	Refers to the stranded agent
3.2 Mission states (spec 7, 11.4)
PENDING --assignAgents(), all agents ready and capable--> ASSIGNED
PENDING --any rejection--> FAILED
ASSIGNED --startMission()--> IN_PROGRESS
ASSIGNED --start check fails--> FAILED
IN_PROGRESS --advanceTime(), progress < duration--> IN_PROGRESS
IN_PROGRESS --advanceTime(), progress >= duration--> COMPLETED
IN_PROGRESS --failure (dust storm, equipment, battery)--> FAILED
A mission starts counting its duration when it becomes IN_PROGRESS. Each time unit adds 1 to its progress.
Only startMission, advanceTime, complete, failMission and assignAgents change a mission's state, and each refuses an illegal move (for example complete() on a PENDING mission does nothing).
Agents are released on both completion and failure. A mission that fails from PENDING never marked its agents busy, so it releases nobody.
Every rejected or failed mission stores a failure reason (getFailureReason()).
3.3 Rejection reasons
A submitted mission becomes FAILED immediately, with a clear reason, if:

another mission is active;
the environment blocks it (for example a dust storm for exploration);
an agent is not registered in the colony;
the required resources are insufficient;
an agent is missing, not ready, or does not possess the required capability.
3.4 One active mission at a time (spec 6.2)
The colony keeps one activeMission. Emergency responses are not subject to this limit, but responders must be available, meaning they are not part of the active mission.

4. Resource rules (spec 5)
Resources are stored in facilities (Facility.R1) and managed at colony level.
Colony-wide total of a resource = the sum over all operational facilities. Stock in a destroyed or unavailable facility does not count.
A request may be supplied by several facilities. With 300 oxygen in one facility and 200 in another, a request for 450 is valid and one for 600 is not.
All-or-nothing: the colony checks the whole request before touching anything. If any single resource is short, nothing is consumed, the operation fails with a reason such as Insufficient FUEL: required 500.0, available 100.0., and the failed mission never takes any agent.
Quantities never go negative: removal refuses to exceed the stock, and an oxygen leak is clamped at zero.
The colony status report shows colony-wide totals.
Consumption timing: a mission's resources are consumed when the colony accepts the mission (confirm against Colony.java).
Allocation policy: when a request is drawn from several facilities, facilities supply in the order they were added to the colony (confirm against Colony.java).
Resources already consumed by an accepted mission are not refunded if the mission later fails (confirm against Colony.java).
5. Emergency rules (spec 9)
5.1 General rules
An emergency has a type, an affected location (a facility, or the Martian surface), and a status: ACTIVE, RESOLVED or UNRESOLVED.
A responder is appropriate if it reports that it can handle the emergency type (canHandle). The concrete type is never checked.
A responder must be available (not on the active mission) and usable.
Dispatching moves the responders to the affected facility.
Emergencies do not interrupt the active mission.
If the required responders are unavailable: the emergency is marked UNRESOLVED, a clear reason is recorded, no response resources are consumed, and it is not retried automatically.
Responders are released when the response ends.
5.2 Required responders per type (our decisions)
Emergency	Location	Required responders
Oxygen leak	A habitat	at least 1 human and 1 robot able to repair (Engineer and RepairRobot)
Fire	A laboratory	1 human and 1 robot
Rover breakdown	Martian surface	1 robot
Medical emergency	A facility	1 human
5.3 Oxygen leak (spec 9.2)
The leak is triggered manually with Colony.handleEmergency(...).
The affected habitat is identified.
The scenario's loss amount (for example 50) is deducted from that habitat only, which lowers the colony total. This is a one-time deduction, not a continuous drain.
Responders are chosen and checked for availability.
Response resources are checked: a repair uses 2 spare parts and 5 battery units on the repair robot.
If everything is available, responders are dispatched, the leak is repaired, the resources are consumed, and the emergency is RESOLVED.
If not, the same amount is deducted again, so an unresolved leak costs twice the oxygen of a repaired one. The emergency is UNRESOLVED with a reason and nothing is consumed.
A habitat's oxygen never falls below zero.
A leaking habitat stays OPERATIONAL, so its other stock (water, food) still counts toward the colony totals.
5.4 Current limitations for the other emergencies
Only Engineer and RepairRobot report that they can handle an emergency (OXYGEN_LEAK). Scientist, MedicalOfficer, ExplorationRover and Drone report none, so fire, rover breakdown and medical emergencies have their types, requirements and effects defined (Laboratory.catchFire(), ExplorationRover.breakDown()) but no responder yet reports that it can handle them, so those emergencies would end UNRESOLVED.

6. Environmental effects (spec 8)
Condition	Effects
Normal	No restriction
Dust storm	Exploration missions cannot start. Rovers cannot operate. An active exploration (rover) mission fails.
Communication disruption	Rescue missions cannot start. Remotely controlled drones cannot be controlled.
Repair missions are indoor work and are not blocked by either condition.

Missions already in progress when conditions change: if the new condition is incompatible with a running mission (a dust storm during an exploration), the mission is moved to FAILED with a reason that names the condition, and its agents are released. Resources it already consumed are not refunded. Missions that the new condition does not affect continue normally.

7. UML design (spec 11)
All five diagram types are in MARS-OPS-UML.drawio (open with https://app.diagrams.net). Each diagram is one page.

Page	Diagram
1	Class diagram
2	Object diagram (scenario 1 immediately after the colony accepts the mission)
3	State machine diagram (mission lifecycle)
4	Sequence diagram A: mission submission and execution
5	Sequence diagram B: oxygen leak emergency
6	Sequence diagram C: environmental restriction (dust storm)
7	Use case diagram
Exported images/PDF of the same diagrams are included in the uml/ folder.

8. Compiling and running
Requirements: JDK 9 or later (the code uses List.of and Map.of).

NetBeans: open the project, then Run → Run Project (or press F6). The main class is lab5.main.

Command line, from the folder that contains the lab5 source folder:

javac -d out lab5/*.java
java -cp out lab5.main
9. Running the demonstration scenarios (spec 12)
Running lab5.main executes all five required scenarios in order, then extra unit-style checks. Each scenario builds a fresh colony so scenarios never affect each other.

Scenario	Method in main	Expected result
1. Successful exploration	scenario1SuccessfulExploration()	Scientist and rover are accepted. The mission goes ASSIGNED → IN_PROGRESS → COMPLETED after 3 time units. The status report is printed before and after. Agents are available again, the rover's battery has dropped.
2. Mission rejection	scenario2MissionRejection()	The scientist lacks the EXPLORATION skill. The mission is FAILED immediately with "does not possess the required capability" and never becomes active.
3. Resource shortage	scenario3ResourceShortage()	The mission asks for 500 fuel while the colony has 100. It is rejected with Insufficient FUEL ..., fuel stays at 100, nothing goes negative.
4. Emergency handling	scenario4EmergencyHandling()	4a: responders available, leak repaired, oxygen 300 → 250, spare parts 10 → 8, robot battery 100 → 95, RESOLVED. 4b: robot unavailable, UNRESOLVED with a reason, oxygen 300 → 200 (deducted twice), nothing consumed.
5. Environmental effect	scenario5EnvironmentalEffect()	5a: during a dust storm the exploration cannot start: "Surface operations are not allowed during a dust storm." 5b: a storm during an active mission fails it and releases the agents.
To run a single scenario, comment out the other calls at the top of main().

10. Testing
The main class (lab5.main) is the test driver required by the spec. It contains the five scenarios above and two extra groups:

runResourceTests(): adding, removing and checking resources, invalid types, insufficient requests, and a successful transaction.
runCrewTests(): health and fatigue clamping, usability thresholds, and the isReady() rule.
11. Known limitations
MissionType.RESEARCH exists, but there is no research mission class yet.
There is no dedicated storage facility class.
Laboratory keeps its equipment as a list of names, so facilities are not linked to the Equipment class. Only rovers carry Equipment objects.
Facility status is stored as a String.
Fire, rover breakdown and medical emergencies are modelled but not yet answered by any responder (see 5.4).
The Resources class prints a message on every add or remove, which makes the console output noisy.
12. Project structure for submission
StudentID-Prog-II-Lab5/
├── lab5/            (all .java source files)
├── uml/             (MARS-OPS-UML.drawio and exported PDF/PNG)
└── README.md
