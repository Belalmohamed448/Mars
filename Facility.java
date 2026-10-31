/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab5;

/**
 *
 * @author pc
 */
public abstract class Facility {
    protected String id;
    protected String name;
    protected String status;
    public  Resources R1 = new Resources();

    Facility(String id, String name){
        this.id = id;
        this.name = name;
        this.status = "OPERATIONAL";
    }

    public void GetFacilityInfo(){
        System.out.println("========== FACILITY INFO ============");
        System.out.println(">>> ID     : " + this.id);
        System.out.println(">>> Name   : " + this.name);
        System.out.println(">>> Status : " + this.status);
    }

    public void setStatuesOPERATIONAL(){
        this.status = "OPERATIONAL";
        return;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatuesUNAVAILABLE(){
        this.status = "UNAVAILABLE";
        return;
    }

    public void setStatuesDESTROYED(){
        this.status = "DESTROYED";
        return;
    }

    public void addResource(String type,double amount){
        R1.AddResources(type, amount);
        return;
    }

    public void RemoveResource(String type,double amount){
        R1.RemoveResources(type, amount);
        return;
    }

    public Boolean isOperational(){
        if(this.status.equals("OPERATIONAL")){
            return true;
        }else{
            return false;
        }
    }
    
    public void GetFacilityStock(){
        R1.DisplayResources();
    }
}
