package lab5;

import java.util.List;

public class RepairMission extends Mission {

    private static final int REPAIR_MIN_BATTERY = 5; 
    public RepairMission(int id, String name, int duration, Resources resourceRequest, Facility target) {
        super(id, name, MissionType.REPAIR, duration, resourceRequest,
                List.of(requireTarget(target)), REPAIR_MIN_BATTERY);
    }

    private static Facility requireTarget(Facility target) {
        if (target == null) {
            throw new IllegalArgumentException("Repair mission needs a target facility.");
        }
        return target;
    }

    public Facility getTarget() {
        return getTargetFacilities().get(0);
    }

    @Override
    public boolean canStartIn(Environment environment) {
        return true;   // indoor repair: not blocked by dust storms or disruption
    }
}