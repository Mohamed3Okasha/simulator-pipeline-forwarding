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
public class Operator {
    
    public String name;
    public int executionCycles;
    public String functionalUnit;
    public String displayValue;
    
    public Operator(String name, int executionCycles, String functionalUnit, String displayValue){
        this.name = name;
	this.executionCycles = executionCycles;
	this.functionalUnit = functionalUnit;
	this.displayValue = displayValue;
    }
    
    public void changeExecutionTime(int newCycles){
        this.executionCycles = newCycles;
    }
    
}
