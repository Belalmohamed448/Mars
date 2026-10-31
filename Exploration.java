package lab5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exploration extends Mission {

    private final String region;
    private final List<EquipmentType> requiredEquipment;

    public Exploration(int id, String name, int duration,
            Resources resourceRequest,
            List<Facility> targetFacilities,
            int minBattery,
            String region,
            List<EquipmentType> requiredEquipment) {

        super(id, name, MissionType.EXPLORATION, duration,
                resourceRequest, targetFacilities, minBattery);

        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Region cannot be empty."
            );
        }

        if (requiredEquipment == null) {
            throw new IllegalArgumentException(
                    "Required equipment list cannot be null."
            );
        }

        this.region = region;
        this.requiredEquipment = new ArrayList<>(requiredEquipment);
    }

    @Override
    public String getStartBlockedReason() {
        return "Surface operations are not allowed during a dust storm.";
    }

    public String getRegion() {
        return region;
    }

    @Override
    public List<EquipmentType> getRequiredEquipment() {
        return Collections.unmodifiableList(requiredEquipment);
    }

    @Override
    public boolean canStartIn(Environment environment) {
        return environment != null && !environment.isDustStorm();
    }

    public boolean execute(Colony colony) {

        if (colony == null) {
            return false;
        }

        if (getState() != MissionStatus.ASSIGNED) {
            return false;
        }

        Mission active = colony.getActiveMission();

        if (active != null && active != this && active.isActive()) {
            failMission("Another mission is currently active.");
            return false;
        }

        if (active != this) {
            failMission("This mission has not been accepted by the colony.");
            return false;
        }

        Environment environment = colony.getEnvironment();

        if (!canStartIn(environment)) {
            failMission(
                    "Exploration cannot start during a dust storm."
            );
            return false;
        }

        ExplorationRover rover = null;

        for (Agent agent : getAssignedAgents()) {
            if (agent instanceof ExplorationRover) {
                rover = (ExplorationRover) agent;
                break;
            }
        }

        if (rover == null) {
            failMission("No exploration rover is assigned.");
            return false;
        }

        if (!rover.isUsable()) {
            failMission(
                    "The exploration rover is unavailable or unusable."
            );
            return false;
        }

        if (rover.getBatteryLevel() < getMinBattery()) {
            failMission(
                    "The rover battery is below the mission minimum."
            );
            return false;
        }

        if (!rover.canPerform(this)) {
            failMission(
                    "The rover lacks sufficient battery or required equipment."
            );
            return false;
        }

        startMission();
        rover.explore(region);

        return true;
    }
}
