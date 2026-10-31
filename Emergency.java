package lab5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Emergency {

    private final int id;
    private final EmergencyType type;
    private final Facility affectedFacility;     
    private final double oxygenLoss;             
    
    private EmergencyStatus status;
    private String unresolvedReason;
    private final ArrayList<Agent> responders;

    public Emergency(int id, EmergencyType type, Facility affectedFacility, double oxygenLoss) {
        if (type == null) {
            throw new IllegalArgumentException("Emergency type cannot be null.");
        }
        if (oxygenLoss < 0) {
            throw new IllegalArgumentException("Oxygen loss cannot be negative.");
        }
        if ((type == EmergencyType.OXYGEN_LEAK || type == EmergencyType.FIRE)
                && affectedFacility == null) {
            throw new IllegalArgumentException(type + " needs an affected facility.");
        }
        this.id = id;
        this.type = type;
        this.affectedFacility = affectedFacility;
        this.oxygenLoss = oxygenLoss;
        this.status = EmergencyStatus.ACTIVE;
        this.unresolvedReason = null;
        this.responders = new ArrayList<>();
    }

    public int getId() { return id; }
    public EmergencyType getType() { return type; }
    public Facility getAffectedFacility() { return affectedFacility; }
    public double getOxygenLoss() { return oxygenLoss; }
    public EmergencyStatus getStatus() { return status; }
    public String getUnresolvedReason() { return unresolvedReason; }

    public List<Agent> getResponders() {
        return Collections.unmodifiableList(responders);
    }

    public String getLocationName() {
        return affectedFacility == null ? "Martian Surface" : affectedFacility.getName();
    }

    public boolean isActive() { return status == EmergencyStatus.ACTIVE; }
    public boolean isFinished() { return status != EmergencyStatus.ACTIVE; }

    
    public int getRequiredHumans() {
        switch (type) {
            case OXYGEN_LEAK:        return 1;  
            case FIRE:               return 1;
            case MEDICAL_EMERGENCY:  return 1;
            default:                 return 0;
        }
    }

    public int getRequiredRobots() {
        switch (type) {
            case OXYGEN_LEAK:        return 1;   
            case FIRE:               return 1;
            case ROVER_BREAKDOWN:    return 1;
            default:                 return 0;
        }
    }

    
    public boolean dispatch(List<Agent> team) {
        if (status != EmergencyStatus.ACTIVE || !responders.isEmpty()) {
            return false;
        }
        if (team == null || team.isEmpty()) {
            markUnresolved("No responders were available.");
            return false;
        }
        for (Agent a : team) {
            if (a == null) {
                markUnresolved("A required responder is missing.");
                return false;
            }
            if (!a.isReady()) {
                markUnresolved("Responder " + a.getName() + " is not available or not in usable condition.");
                return false;
            }
            if (!a.canHandle(this)) {
                markUnresolved("Responder " + a.getName() + " cannot handle " + type + ".");
                return false;
            }
        }
        for (Agent a : team) {
            if (!responders.contains(a)) {
                responders.add(a);
                a.setAvailability(false);
                a.setCurrentLocation(getLocationName());  
            }
        }
        return true;
    }

    
    public void resolve() {
        if (status != EmergencyStatus.ACTIVE) {
            return;
        }
        status = EmergencyStatus.RESOLVED;
        releaseResponders();
    }

   
    public void markUnresolved(String reason) {
        if (status != EmergencyStatus.ACTIVE) {
            return;
        }
        status = EmergencyStatus.UNRESOLVED;
        unresolvedReason = reason;
        releaseResponders();
    }

    private void releaseResponders() {
        for (Agent a : responders) {
            a.setAvailability(true);
        }
    }

    public String getStatusSummary() {
        return "Emergency | #" + id + " | " + type
                + " | Location: " + getLocationName()
                + " | Status: " + status
                + (unresolvedReason != null ? " | Reason: " + unresolvedReason : "");
    }
}