package lab5;

import java.util.ArrayList;

public class Engineer extends CrewMember {

    public Engineer(ArrayList<String> Skills, int FatigueLevel, int HealthStatus, String Name, int id, boolean availability, String currentLocation) {
        super(Skills, FatigueLevel, HealthStatus, Name, id, availability, currentLocation);
    }

    @Override
    public boolean canHandle(Emergency e) {
        return e != null && e.getType() == EmergencyType.OXYGEN_LEAK;
    }

    @Override
    protected String getRole() {
        return "Engineer";
    }

    public void extinguishLabFire(Laboratory lab) {
        if (isUsable()) {
            lab.extinguishFire();
            increaseFatigue(10);
            setCurrentLocation("LABORATORY");
        } else {
            System.out.println("System Message : Engineer is CANNOT Be Used . . . ");
        }
    }

    public void sealHabitatLeak(Habitat hab) {
        if (isUsable()) {
            hab.sealLeak();
            increaseFatigue(10);
            setCurrentLocation("HABITAT");
        } else {
            System.out.println("System Message : Engineer is CANNOT Be Used . . . ");
        }
    }

    public void setLinkComm(CommunicationStation com) {
        if (isUsable()) {
            com.setLink(true);
            increaseFatigue(10);
            setCurrentLocation("COMMUNICATIONSTATION");
        } else {
            System.out.println("System Message : Engineer is CANNOT Be Used . . . ");
        }
    }

    @Override
    public void update() {
        super.update();
    }
}
