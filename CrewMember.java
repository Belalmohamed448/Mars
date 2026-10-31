package lab5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CrewMember extends Agent {

    private static final int MIN_USABLE_HEALTH = 30;
    private static final int MAX_USABLE_FATIGUE = 80;
    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 0;

    private ArrayList<String> skills;
    private int fatigueLevel;
    private int healthStatus;

    public CrewMember(List<String> skills, int fatigueLevel, int healthStatus, String name, int id, boolean availability, String currentLocation) {
        super(name, id, availability, currentLocation);
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        this.fatigueLevel = clamp(fatigueLevel);
        this.healthStatus = clamp(healthStatus);
    }

    public List<String> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public void setSkills(List<String> skills) {
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
    }

    public int getFatigueLevel() {
        return fatigueLevel;
    }

    public int getHealthStatus() {
        return healthStatus;
    }

    public void setFatigueLevel(int fatigueLevel) {
        this.fatigueLevel = clamp(fatigueLevel);
    }

    public void setHealthStatus(int healthStatus) {
        this.healthStatus = clamp(healthStatus);
    }

    private int clamp(int value) {
        return Math.max(MIN_VALUE, Math.min(MAX_VALUE, value));
    }

    public void increaseFatigue(int amount) {
        if (amount < 0) {
            return;
        }
        fatigueLevel = clamp(fatigueLevel + amount);
    }

    public void decreaseFatigue(int amount) {
        if (amount < 0) {
            return;
        }
        fatigueLevel = clamp(fatigueLevel - amount);
    }

    public void heal(int amount) {
        if (amount < 0) {
            return;
        }
        healthStatus = clamp(healthStatus + amount);
    }

    public void damage(int amount) {
        if (amount < 0) {
            return;
        }
        healthStatus = clamp(healthStatus - amount);
    }

    @Override
    public boolean isUsable() {
        return healthStatus > MIN_USABLE_HEALTH && fatigueLevel < MAX_USABLE_FATIGUE;
    }

    @Override
    public boolean canPerform(Mission m) {
        if (m == null || m.getType() == null) {
            return false;
        }
        String missionTypeStr = m.getType().name();
        for (String skill : skills) {
            if (skill.equalsIgnoreCase(missionTypeStr)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public abstract boolean canHandle(Emergency e);

    protected abstract String getRole();

    @Override
    public void update() {
        if (!isAvailable()) {
            increaseFatigue(2);
        } else {
            decreaseFatigue(1);
        }
    }

    @Override
    public String getStatusSummary() {
        return getRole() + " | " + getName() + " (ID " + getId() + ")"
                + " | Health: " + healthStatus
                + " | Fatigue: " + fatigueLevel
                + " | Skills: " + skills
                + " | Location: " + getCurrentLocation()
                + " | Available: " + isAvailable()
                + " | Usable: " + isUsable();
    }
}
