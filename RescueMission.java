package lab5;

public class RescueMission extends Mission {

    private final Agent strandedEntity;

    public RescueMission(int id, String name, int duration, Resources resourceRequest,
            Agent strandedEntity, int minBattery) {
        super(id, name, MissionType.RESCUE, duration, resourceRequest, null, minBattery);
        if (strandedEntity == null) {
            throw new IllegalArgumentException("Rescue mission needs a stranded entity.");
        }
        this.strandedEntity = strandedEntity;
    }

    public Agent getStrandedEntity() {
        return strandedEntity;
    }

    @Override
    public boolean canStartIn(Environment environment) {
        return !environment.isCommunicationDisrupted();
    }

    @Override
    public String getStartBlockedReason() {
        return "Rescue cannot start: communication is disrupted.";
    }
}