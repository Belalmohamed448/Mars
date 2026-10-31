package lab5;

public class Drone extends Robot {

    private static final int DRAIN_RATE = 4;

    private final int flightRange;
    private final boolean remoteControlled;

    public Drone(String name, int id, String currentLocation, int flightRange) {
        super(name, id, currentLocation, DRAIN_RATE);
        this.flightRange = flightRange;
        this.remoteControlled = true;
    }

    public int getFlightRange() {
        return flightRange;
    }

    public boolean isRemoteControlled() {
        return remoteControlled;
    }

    public void performReconnaissance(String region) {
        System.out.println(getName() + " is performing aerial reconnaissance over " + region + ".");
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
        return hasEnoughBattery(m.getMinBattery());
    }

    @Override
    public boolean canHandle(Emergency e) {
        return false;   
    }

    @Override
    public boolean canOperateIn(Environment env) {
         
        return !remoteControlled || !env.isCommunicationDisrupted();
    }

    @Override
    public String getStatusSummary() {
        return super.getStatusSummary()
                + " | Range: " + flightRange
                + " | Remote: " + remoteControlled;
    }
}
