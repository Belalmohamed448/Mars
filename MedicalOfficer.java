package lab5;

import java.util.ArrayList;

public class MedicalOfficer extends CrewMember {

    public MedicalOfficer(ArrayList<String> Skills, int FatigueLevel, int HealthStatus, String Name, int id, boolean availability, String currentLocation) {
        super(Skills, FatigueLevel, HealthStatus, Name, id, availability, currentLocation);
    }

    @Override
    public boolean canHandle(Emergency e) {
        return false;
    }

    @Override
    protected String getRole() {
        return "Medical Officer";
    }

    public void treat(CrewMember patient) {
        if (patient == this) {
            System.out.println("System Message : Medical Officer cannot treat themselves.");
            return;
        }
        if (!isUsable()) {
            System.out.println("System Message : Medical Officer CANNOT be used...");
            return;
        }
        setCurrentLocation(patient.getCurrentLocation());
        patient.heal(100);
        increaseFatigue(10);
        System.out.println("System Message : Patient Was Healed Successfully !!!");
    }

    @Override
    public void update() {
        super.update();
    }
}
