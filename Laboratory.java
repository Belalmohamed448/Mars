package lab5;

import java.util.ArrayList;
import java.util.List;

public class Laboratory extends Facility {
    private boolean onFire;

    private List<String> equipment = new ArrayList<>();
    private List<String> damagedEquipment = new ArrayList<>();

    Laboratory(String id, String name){
        super(id, name);
    }

    public void catchFire() {
        if (onFire) return;
        onFire = true;
        setStatuesUNAVAILABLE();
        damageEquipment();
    }

    public void extinguishFire(){
        onFire = false;
        if (!this.status.equals("DESTROYED")) {
            setStatuesOPERATIONAL();
        }
    }

    public boolean isOnFire(){return onFire;}

    public void addEquipment(String name){
        equipment.add(name);
    }

    public boolean hasEquipment(String name){
        return equipment.contains(name) && !damagedEquipment.contains(name);
    }

    public void damageEquipment(){ 
        damagedEquipment.addAll(equipment);
    }

    public void repairEquipment(){ 
        damagedEquipment.clear();
    }
}
