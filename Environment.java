/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab5;

/**
 *
 * @author pc
 */


public class Environment {
    public enum EnvironmentCondition {
    NORMAL,
    DUST_STORM,
    COMMUNICATION_DISRUPTION
}
    private EnvironmentCondition condition;

    public Environment() {
        this.condition = EnvironmentCondition.NORMAL;
    }

    public EnvironmentCondition getCondition() { return condition; }

    public void setCondition(EnvironmentCondition condition) {
        if (condition == null) {
            System.out.println("Condition cannot be null");
        }else
        this.condition = condition;
    }

    public boolean isDustStorm() {
        return condition == EnvironmentCondition.DUST_STORM;
    }

    public boolean isCommunicationDisrupted() {
        return condition == EnvironmentCondition.COMMUNICATION_DISRUPTION;
    }

    public boolean isNormal() {
        return condition == EnvironmentCondition.NORMAL;
    }
    public String getDescription(){
    if(condition == EnvironmentCondition.NORMAL)
        return "Normal";
    else if(condition == EnvironmentCondition.DUST_STORM)
        return " Dust Storm";
    else
        return " Communication is disrupted";
    
    }
}

