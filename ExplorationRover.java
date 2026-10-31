package lab5;

import java.util.ArrayList;
import java.util.List;

public class ExplorationRover extends Robot {

    private static final int DRAIN_RATE = 5;

    private final int carryCapacity;
    private final List<Equipment> equipment;

    public ExplorationRover(String name, int id, String currentLocation, int carryCapacity) {
        super(name, id, currentLocation, DRAIN_RATE);
        this.carryCapacity = carryCapacity;
        this.equipment = new ArrayList<>();
    }

    public int getCarryCapacity() {
        return carryCapacity;
    }

    public List<Equipment> getEquipment() {
        return new ArrayList<>(equipment);
    }

    public boolean loadEquipment(Equipment e) {
        if (e == null || equipment.size() >= carryCapacity) {
            return false;
        }
        equipment.add(e);
        return true;
    }

    public boolean unloadEquipment(Equipment e) {
        return equipment.remove(e);
    }

    public boolean hasEquipmentOfType(EquipmentType type) {
        for (Equipment e : equipment) {
            if (e.getType() == type && e.isUsable()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAllRequired(List<EquipmentType> required) {
        for (EquipmentType t : required) {
            if (!hasEquipmentOfType(t)) {
                return false;
            }
        }
        return true;
    }

    
    public void explore(String region) {
        setCurrentLocation("SURFACE");
        System.out.println(getName() + " is exploring " + region + ".");
    }

   
    public void breakDown() {
        damage();
        System.out.println(getName() + " has broken down at " + getCurrentLocation() + ".");
    }


    @Override
    public void onMissionStart(Mission m) {
        if (m != null && m.getType() == MissionType.EXPLORATION) {
            explore(m.getName());
        }
    }

    @Override
    public boolean canPerform(Mission m) {
        if (m == null || !isUsable()) {
            return false;
        }
        MissionType t = m.getType();
        if (t != MissionType.EXPLORATION && t != MissionType.RESCUE) {
            return false;
        }
        if (!hasEnoughBattery(m.getMinBattery())) {
            return false;
        }
        return hasAllRequired(m.getRequiredEquipment());
    }

    @Override
    public boolean canHandle(Emergency e) {
        return false;   
    }

    @Override
    public boolean canOperateIn(Environment env) {
        return !env.isDustStorm();
    }

    @Override
    public String getStatusSummary() {
        return super.getStatusSummary() + " | Equipment: " + equipment.size() + "/" + carryCapacity;
    }
}