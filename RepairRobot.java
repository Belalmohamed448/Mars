package lab5;

public class RepairRobot extends Robot {

    private static final int DRAIN_RATE = 3;

    private final int sparePartsPerRepair;
    private final int batteryPerRepair;

    public RepairRobot(String name, int id, String currentLocation) {
        super(name, id, currentLocation, DRAIN_RATE);
        this.sparePartsPerRepair = 2;
        this.batteryPerRepair = 5;
    }

    public int getSparePartsPerRepair() {
        return sparePartsPerRepair;
    }

    public int getBatteryPerRepair() {
        return batteryPerRepair;
    }

    public boolean performRepair(Facility f) {
        if (f == null || !isUsable() || !hasEnoughBattery(batteryPerRepair)) {
            return false;   
        }
        drainBattery(batteryPerRepair);
        f.setStatuesOPERATIONAL();   
        System.out.println(getName() + " repaired " + f.getName() + ".");
        return true;
    }

    @Override
    public boolean canPerform(Mission m) {
        if (m == null || !isUsable()) {
            return false;
        }
        if (m.getType() != MissionType.REPAIR) {
            return false;
        }
        return hasEnoughBattery(m.getMinBattery());
    }

    @Override
    public boolean canHandle(Emergency e) {
        
        return e != null && e.getType() == EmergencyType.OXYGEN_LEAK;
    }

    @Override
    public boolean canOperateIn(Environment env) {
        return true;   
    }

    @Override
    public String getStatusSummary() {
        return super.getStatusSummary()
                + " | Repair cost: " + sparePartsPerRepair + " parts, "
                + batteryPerRepair + " battery";
    }
}
