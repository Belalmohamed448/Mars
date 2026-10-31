package lab5;

import java.util.ArrayList;

public class Scientist extends CrewMember {

    public Scientist(ArrayList<String> Skills, int FatigueLevel, int HealthStatus, String Name, int id, boolean availability, String currentLocation) {
        super(Skills, FatigueLevel, HealthStatus, Name, id, availability, currentLocation);
    }

    @Override
    public boolean canHandle(Emergency e) {
        return false;
    }

   
    @Override
    public void onMissionStart(Mission m) {
        if (m != null && m.getType() == MissionType.RESEARCH) {
            System.out.println(getName() + " has started the research mission '"
                    + m.getName() + "'.");
        }
    }
    
    public void conductResearch(Laboratory lab) {
    if (!isUsable() || !lab.isOperational()) {
        System.out.println("System Message : Scientist cannot conduct research.");
        return;
    }
    setCurrentLocation(lab.name);
    increaseFatigue(10);
    System.out.println("System Message : Research Was Conducted Successfully 🙂 !!.");
    }

    @Override
    protected String getRole() {
        return "Scientist";
    }

    @Override
    public void update() {
        super.update();
    }
}