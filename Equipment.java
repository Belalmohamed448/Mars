package lab5;

public class Equipment {

    private final int id;
    private final String name;
    private final EquipmentType type;
    private EquipmentCondition condition;
    private String currentLocation;

    public Equipment(int id, String name, EquipmentType type, EquipmentCondition condition, String currentLocation) {
        if (type == null) {
            throw new IllegalArgumentException("Equipment type cannot be null.");
        }
        if (condition == null) {
            throw new IllegalArgumentException("Equipment condition cannot be null.");
        }
        this.id = id;
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.currentLocation = currentLocation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EquipmentType getType() {
        return type;
    }

    public EquipmentCondition getCondition() {
        return condition;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isUsable() {
        return condition == EquipmentCondition.WORKING;
    }

    public void damage() {
        if (condition != EquipmentCondition.DESTROYED) {
            this.condition = EquipmentCondition.DAMAGED;
        }
    }

    public void destroy() {
        this.condition = EquipmentCondition.DESTROYED;
    }

    public void repair() {
        if (condition == EquipmentCondition.DAMAGED) {
            this.condition = EquipmentCondition.WORKING;
        }
    }

    public String getStatusSummary() {
        return "Equipment | " + name + " (ID " + id + ")"
                + " | Type: " + type
                + " | Condition: " + condition
                + " | Location: " + currentLocation
                + " | Usable: " + isUsable();
    }
}