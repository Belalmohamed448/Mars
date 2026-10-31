package lab5;

public class Simulation {

    private final Colony colony;

    public Simulation(Colony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null.");
        }
        this.colony = colony;
    }

    public Colony getColony() {
        return colony;
    }

    public void advanceTime(int units) {
        for (int i = 0; i < units; i++) {
            colony.advanceTime();
        }
    }

    public void advanceTime() {
        advanceTime(1);
    }
}