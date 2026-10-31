package lab5;

public class PowerStation extends Facility {
    PowerStation(String id,String name){
        super(id, name);
    }

    public void GeneratePower(double amount){
        R1.AddResources("POWER", amount);
        return;
    }

    public void GenerateFuel(double amount){
        R1.AddResources("FUEL", amount);
        return;
    }
}
