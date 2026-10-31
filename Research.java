package lab5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Research extends Mission {

    private final String experimentName;
    private final List<String> requiredLabEquipment;

    // research is done by scientists in a laboratory, so no battery is needed (minBattery = 0)
    public Research(int id, String name, int duration, Resources resourceRequest,
            List<Facility> targetFacilities, String experimentName,
            List<String> requiredLabEquipment) {

        super(id, name, MissionType.RESEARCH, duration,
                resourceRequest, targetFacilities, 0);

        if (experimentName == null || experimentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Experiment name cannot be empty.");
        }
        if (requiredLabEquipment == null) {
            throw new IllegalArgumentException("Required equipment list cannot be null.");
        }

        this.experimentName = experimentName;
        this.requiredLabEquipment = new ArrayList<>(requiredLabEquipment);
    }

    public String getExperimentName() {
        return experimentName;
    }

    // named differently from Mission.getRequiredEquipment() on purpose:
    // that one returns List<EquipmentType> (used by rovers), this one is laboratory equipment by name
    public List<String> getRequiredLabEquipment() {
        return Collections.unmodifiableList(requiredLabEquipment);
    }

    // the laboratory is one of the mission's target facilities (spec 6.3)
    private Laboratory getLaboratory() {
        for (Facility facility : getTargetFacilities()) {
            if (facility instanceof Laboratory) {
                return (Laboratory) facility;
            }
        }
        return null;
    }

    private List<String> missingEquipment(Laboratory lab) {
        List<String> missing = new ArrayList<>();
        for (String item : requiredLabEquipment) {
            if (item == null || item.trim().isEmpty() || !lab.hasEquipment(item)) {
                missing.add(item);
            }
        }
        return missing;
    }

    @Override
    public boolean canStartIn(Environment environment) {
        if (environment == null) {
            return false;
        }
        Laboratory lab = getLaboratory();
        if (lab == null) {
            return false;
        }
        if (!lab.isOperational() || lab.isOnFire()) {
            return false;
        }
        return missingEquipment(lab).isEmpty();
    }

    // a clear reason for every way the research can be blocked (spec 6.2 rule 4)
    @Override
    public String getStartBlockedReason() {
        Laboratory lab = getLaboratory();
        if (lab == null) {
            return "Research needs a laboratory as its target facility.";
        }
        if (lab.isOnFire()) {
            return "The laboratory " + lab.getName() + " is on fire.";
        }
        if (!lab.isOperational()) {
            return "The laboratory " + lab.getName() + " is not operational.";
        }
        List<String> missing = missingEquipment(lab);
        if (!missing.isEmpty()) {
            return "Required laboratory equipment is missing or damaged: " + missing + ".";
        }
        return super.getStartBlockedReason();
    }
}