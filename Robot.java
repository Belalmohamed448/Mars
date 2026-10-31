package lab5;

public abstract class Robot extends Agent {

    private static final int MAX_BATTERY = 100;
    private static final int RESERVE = 10;        
    private static final int IDLE_RECHARGE = 2;   

    private int batteryLevel;
    private HealthStatus healthStatus;
    private final int batteryDrainPerUnit;

    public Robot(String name, int id, String currentLocation, int batteryDrainPerUnit) {
        super(name, id, true, currentLocation);   
        this.batteryLevel = MAX_BATTERY;
        this.healthStatus = HealthStatus.OPERATIONAL;
        this.batteryDrainPerUnit = batteryDrainPerUnit;
    }


    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        if (batteryLevel < 0 || batteryLevel > MAX_BATTERY) {
            throw new IllegalArgumentException("Battery must be between 0 and " + MAX_BATTERY);
        }
        this.batteryLevel = batteryLevel;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public int getBatteryDrainPerUnit() {
        return batteryDrainPerUnit;
    }


    public void drainBattery(int amount) {
        if (amount <= 0) {
            return;
        }
        batteryLevel = Math.max(0, batteryLevel - amount);
    }

    public void recharge(int amount) {
        if (amount <= 0) {
            return;
        }
        batteryLevel = Math.min(MAX_BATTERY, batteryLevel + amount);
    }

   
    public boolean hasEnoughBattery(int required) {
        return batteryLevel >= required + RESERVE;
    }


    public void damage() {
        if (healthStatus == HealthStatus.OPERATIONAL) {
            healthStatus = HealthStatus.DAMAGED;
        }
    }

    public void repair() {
        if (healthStatus == HealthStatus.DAMAGED) {
            healthStatus = HealthStatus.OPERATIONAL;
        }
    }

    @Override
    public void update() {
        if (!isAvailable()) {
            drainBattery(batteryDrainPerUnit);   
        } else {
            recharge(IDLE_RECHARGE);             
        }
    }

    @Override
    public boolean isUsable() {
        return healthStatus == HealthStatus.OPERATIONAL && batteryLevel > 0;
    }

    @Override
    public String getStatusSummary() {
        String summary = getName() + " (ID " + getId() + ")"
                + " | Location: " + getCurrentLocation()
                + " | Battery: " + batteryLevel + "/" + MAX_BATTERY
                + " | Health: " + healthStatus;
        if (batteryLevel < 20) {
            summary += " | LOW BATTERY";
        }
        return summary;
    }

  

    public abstract boolean canOperateIn(Environment env);

}
