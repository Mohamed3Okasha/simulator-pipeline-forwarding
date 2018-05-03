/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author Moham
 */
public class Instruction {
    
    private String name;
    public String sourceRegister1;
    public String sourceRegister2;
    public String destinationRegister;
    public Operator operator;
    public Object issueCycle;
    public Object piplineStage;
    public int excuteCounter;
    
    public Instruction(String name, String sourceRegister1, String sourceRegister2,
            String destinationRegister, Operator operator){
        this.name = name;
        this.sourceRegister1 = sourceRegister1;
        this.sourceRegister2 = sourceRegister2;
        this.destinationRegister = destinationRegister;
        this.operator = operator;
        this.issueCycle = null;
        this.piplineStage = null;
        this.excuteCounter = 0;
    }
    
    public String getInstructionData(){
        String instructionData = operator.name;
        if(destinationRegister != null)
            instructionData += " " + destinationRegister;
        if(sourceRegister1 != null)
            instructionData += ", " + sourceRegister1;
        if(sourceRegister2 != null)
            instructionData += ", " + sourceRegister2;
        return instructionData;
    }
    
}
