package lab5;
import java.util.HashMap;
import java.util.Map;

/**
 * Resources
 */
public class Resources {
    private double oxygen = 0 ;
    private double water = 0;
    private double food = 0;
    private double fuel = 0;
    private double power = 0;
    private double medicalSupplies = 0;
    private double spareParts = 0;

    private Map<String, Double> RequestRec = new HashMap<>();


    public double getOxygen(){return this.oxygen;}
    public double getWater(){return this.water;}
    public double getFood(){return this.food;}
    public double getFuel(){return this.fuel;}
    public double getPower(){return this.power;}
    public double getMedicalSupplies(){return this.medicalSupplies;}
    public double getSpareParts(){return this.spareParts;}

    public void DisplayResources(){
        System.out.println();
        System.out.println("==========!   RESOURCES REPORT   !==========");
        System.out.println(">>> Oxygen           : "+this.oxygen);
        System.out.println(">>> Spare Parts      : "+this.spareParts);
        System.out.println(">>> Medical Supplies : "+this.medicalSupplies);
        System.out.println(">>> Fuel             : "+this.fuel);
        System.out.println(">>> Power            : "+this.power);
        System.out.println(">>> Food             : "+this.food);
        System.out.println(">>> Water            : "+this.water);
    }

    public void AddResources(String s, double amount){
        if(amount < 0){throw new IllegalArgumentException("System Message : Invalid Amount Entered . . .");}
        if(s.equals("OXYGEN")){this.oxygen = this.oxygen + amount;}
        else if(s.equals("FOOD")){this.food = this.food + amount; }
        else if(s.equals("WATER")){this.water = this.water + amount; }
        else if(s.equals("FUEL")){this.fuel = this.fuel + amount; }
        else if(s.equals("POWER")){this.power = this.power + amount;}
        else if(s.equals("MEDICAL_SUPPLIES")){this.medicalSupplies = this.medicalSupplies + amount;}
        else if(s.equals("SPARE_PARTS")){this.spareParts = this.spareParts + amount;}
        else {
            System.out.println("System Message : Make Sure You Used One Of The Following Resources Types . . .");
            System.out.println("System Message : [FOOD] [WATER] [FUEL] [OXYGEN] [POWER] [MEDICAL_SUPPLIES] [SPARE_PARTS] ");
            System.out.println();
            return;
        }
        System.out.println("System Message : "+ s + " Amount Was Added Successfully :) !");
    }

    public boolean RemoveResources(String s, double amount){
        if(amount < 0){throw new IllegalArgumentException("System Message : Invalid Amount Entered . . .");}
        if(s.equals("OXYGEN") && this.oxygen >= amount){this.oxygen = this.oxygen - amount;}
        else if(s.equals("FOOD") && this.food >= amount){this.food = this.food - amount; }
        else if(s.equals("WATER") && this.water >= amount){this.water = this.water - amount; }
        else if(s.equals("FUEL") && this.fuel >= amount){this.fuel = this.fuel - amount; }
        else if(s.equals("POWER") && this.power >= amount){this.power = this.power - amount;}
        else if(s.equals("MEDICAL_SUPPLIES") && this.medicalSupplies >= amount){this.medicalSupplies = this.medicalSupplies - amount;}
        else if(s.equals("SPARE_PARTS") && this.spareParts >= amount){this.spareParts = this.spareParts - amount;}
        else {
            System.out.println("System Message : Invalid resource type or insufficient resources . . .");
            System.out.println("System Message : Resources Available : [FOOD] [WATER] [FUEL] [OXYGEN] [POWER] [MEDICAL_SUPPLIES] [SPARE_PARTS] ");
            System.out.println();
            return false;
        }
        System.out.println("System Message : "+ s +" Amount Was Taken Successfully :) !");
        return true;
    }

    public boolean HasAtLeast(String s , double amount){
        if(amount < 0){throw new IllegalArgumentException("System Message : Invalid Amount Entered . . .");}
        if(s.equals("OXYGEN") && this.oxygen >= amount){System.out.println("System Message : " + s + " Is Available :) !");}
        else if(s.equals("FOOD") && this.food >= amount){System.out.println("System Message : " + s + " Is Available :) !");}
        else if(s.equals("WATER") && this.water >= amount){System.out.println("System Message : " + s + " Is Available :) !"); }
        else if(s.equals("FUEL") && this.fuel >= amount){System.out.println("System Message : " + s + " Is Available :) !");}
        else if(s.equals("POWER") && this.power >= amount){System.out.println("System Message : " + s + " Is Available :) !");}
        else if(s.equals("MEDICAL_SUPPLIES") && this.medicalSupplies >= amount){System.out.println("System Message : " + s + " Is Available :) !");}
        else if(s.equals("SPARE_PARTS") && this.spareParts >= amount){System.out.println("System Message : " + s + " Is Available :) !");}
        else {
            System.out.println("System Message : Invalid resource type or insufficient resources . . .");
            System.out.println("System Message : Resources Available : [FOOD] [WATER] [FUEL] [OXYGEN] [POWER] [MEDICAL_SUPPLIES] [SPARE_PARTS] ");
            System.out.println();
            return false;
        }
        return true;
    }

    public void AddRequirement(String s, double amount){
        if(!s.equals("OXYGEN") && !s.equals("FOOD") && !s.equals("WATER") && !s.equals("FUEL") && !s.equals("POWER") && !s.equals("MEDICAL_SUPPLIES") && !s.equals("SPARE_PARTS")){
            System.out.println("System Message : Invalid resource type . . .");
            System.out.println("System Message : Resources Available : [FOOD] [WATER] [FUEL] [OXYGEN] [POWER] [MEDICAL_SUPPLIES] [SPARE_PARTS] ");
            System.out.println();
        }else{
            RequestRec.put(s, amount);
        }
        return;
    }

    public void ViewRequirments(){
        System.out.println("==========! REQUIRMENTS LIST !==========");
        for (Map.Entry<String, Double> RequestRec : RequestRec.entrySet()) {
            System.out.println(RequestRec.getKey() + ": " + RequestRec.getValue());
        }
        System.out.println();
    }

public Boolean GetRequirments() {
    System.out.println("==========! RESOURCES AFTER TRANSACTION !==========");
    System.out.println("DISCLAIMER: THESE ARE NOT FINAL VALUES YET . . .");
    Map<String, Double> resources = Map.of(
        "OXYGEN", oxygen, "FOOD", food, "WATER", water,
        "FUEL", fuel, "POWER", power,
        "MEDICAL_SUPPLIES", medicalSupplies, "SPARE_PARTS", spareParts
    );
    for (Map.Entry<String, Double> r : RequestRec.entrySet()) {
        double remaining = resources.get(r.getKey()) - r.getValue();
        System.out.println(">>> " + r.getKey() + ": " + remaining);
        if (remaining < 0) {
            System.out.println("Insufficient resources!");
            return false;
        }
    }
    for (Map.Entry<String, Double> r : RequestRec.entrySet()) {
        String s = r.getKey();
        double amount = r.getValue();
        if (s.equals("OXYGEN")) oxygen -= amount;
        else if (s.equals("FOOD")) food -= amount;
        else if (s.equals("WATER")) water -= amount;
        else if (s.equals("FUEL")) fuel -= amount;
        else if (s.equals("POWER")) power -= amount;
        else if (s.equals("MEDICAL_SUPPLIES")) medicalSupplies -= amount;
        else if (s.equals("SPARE_PARTS")) spareParts -= amount;
    }
    RequestRec.clear();
    System.out.println("Requirements taken successfully!");
    return true;
}


        public void ClearRequirements() {
            RequestRec.clear();
        }

        public void LeakOxygen(double amount){
            this.oxygen = Math.max(0, this.oxygen - amount) ;
            System.out.println("System Message : <WARNING> OXYGEN LEAK BY " + amount + "UNITS . . . ");
        }

        public double getOxygenLevel(){
            System.out.println("System Message : Oxygen: " + this.oxygen + " Units  . . .");
            return this.oxygen;
        }

    public Map<String, Double> getRequestRec() {
        return RequestRec;
    }


      

}
