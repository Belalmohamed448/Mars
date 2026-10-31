package lab5;

public class Habitat extends Facility {

    private boolean leaking;

    Habitat(String id, String name) {
        super(id, name);
        this.leaking = false;
    }

    public void applyOxygenLeak(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Leak amount cannot be negative.");
        }
        R1.LeakOxygen(amount);
        this.leaking = true;     
    }

    public double getOxygen() {
        return R1.getOxygen();   
    }

    public boolean isLeaking() {
        return leaking;          
    }

    public void sealLeak() {
        leaking = false;
    }
}