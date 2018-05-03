/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pipelining.forwarding;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import models.Instruction;
import models.Operator;

/**
 *
 * @author Mohamed
 */
public class FXMLDocumentController  implements Initializable {
    
    @FXML private ComboBox cfp_add_sub;
    @FXML private ComboBox cfp_mul;
    @FXML private ComboBox cfp_div;
    @FXML private ComboBox cint_div;
    @FXML private ComboBox cint_add_sub;
    @FXML private ComboBox cint_mul;
    @FXML private ComboBox operator;
    @FXML private ComboBox destination_register;
    @FXML private ComboBox source_register1;
    @FXML private ComboBox source_register2;
    private ObservableList<Integer> cycles;
    private ObservableList<String> operators;
    private ObservableList<String> integerRegisters;
    private ObservableList<String> floatRegisters;
    private boolean forwarding;
    @FXML private CheckBox enableForwarding;
    private int nonForwardDelay;
    private int currentCycle;
    private int numOfInstructions;
    private Operator fp_mult;
    private Operator fp_add;
    private Operator fp_sub;
    private Operator fp_div;
    private Operator fp_ld;
    private Operator fp_sd;
    private Operator int_mult;
    private Operator int_add;
    private Operator int_sub;
    private Operator int_div;
    private Operator int_ld;
    private Operator int_sd;
    private Operator br_taken;
    private Operator br_untaken;
    private ArrayList<Instruction> instructions;
    @FXML private GridPane instructions_table;
    private int numOfCol;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboLists();
        destination_register.setVisibleRowCount(3);
        source_register1.setVisibleRowCount(3);
        source_register2.setVisibleRowCount(3);
        setRegistersData(floatRegisters);
        forwarding = false;
        currentCycle = 0;
        numOfInstructions = 0;
        numOfCol = 0;
        initOperators();
        instructions = new ArrayList<>();
        initEventListeners();
    }
    
    public void onOperatorSelect(){
        String operatorValue = operator.getValue().toString();
        destination_register.disableProperty().set(false);
        source_register1.disableProperty().set(false);
        source_register2.disableProperty().set(false);
	if ("fp_ld".equals(operatorValue) || "fp_sd".equals(operatorValue)){
            destination_register.setItems(floatRegisters);
            source_register2.setItems(integerRegisters);
            loadOffsetRegister(source_register1);
	}
	else if ("fp".equals(operatorValue.substring(0,2))){
            setRegistersData(floatRegisters);
	}
	else if ("int_ld".equals(operatorValue) || "int_sd".equals(operatorValue)){
            loadOffsetRegister(source_register1);
            source_register2.setItems(integerRegisters);
            destination_register.setItems(integerRegisters);
	}
	else if ("int".equals(operatorValue.substring(0,3))){
            setRegistersData(integerRegisters);
	}
	else if ("br".equals(operatorValue.substring(0,2))){
            loadOffsetRegister(source_register1);
            source_register2.setItems(integerRegisters);
            clearRegister(destination_register);
	}
        destination_register.getSelectionModel().selectFirst();
        source_register1.getSelectionModel().selectFirst();
        source_register2.getSelectionModel().selectFirst();
    }
    
    private void setRegistersData(ObservableList<String> list){
        destination_register.setItems(list);
        source_register1.setItems(list);
        source_register2.setItems(list);
        destination_register.getSelectionModel().selectFirst();
        source_register1.getSelectionModel().selectFirst();
        source_register2.getSelectionModel().selectFirst();
    }
    
    private void initComboLists(){
        cycles = FXCollections.observableArrayList(
                1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20
        );
        operators = FXCollections.observableArrayList(
                "fp_add", "fp_sub", "fp_mult", "fp_div", "fp_ld", "fp_sd", "int_add",
                "int_sub", "int_mult", "int_div", "int_ld", "int_sd", "br_taken", "br_untaken"
        );
        integerRegisters = FXCollections.observableArrayList();
        floatRegisters = FXCollections.observableArrayList();
        for(int i = 1; i < 33; ++i){
            integerRegisters.add("R"+i);
        }
        for(int i = 1; i < 17; ++i){
            floatRegisters.add("F"+i);
        }
        cfp_add_sub.setItems(cycles);
        cfp_mul.setItems(cycles);
        cfp_div.setItems(cycles);
        cint_div.setItems(cycles);
        cint_add_sub.setItems(cycles);
        cint_mul.setItems(cycles);
        cfp_add_sub.setVisibleRowCount(3);
        cfp_mul.setVisibleRowCount(3);
        cfp_div.setVisibleRowCount(3);
        cint_div.setVisibleRowCount(3);
        cint_add_sub.setVisibleRowCount(3);
        cint_mul.setVisibleRowCount(3);
        cfp_add_sub.getSelectionModel().selectFirst();
        cfp_mul.getSelectionModel().selectFirst();
        cfp_div.getSelectionModel().selectFirst();
        cint_div.getSelectionModel().selectFirst();
        cint_add_sub.getSelectionModel().selectFirst();
        cint_mul.getSelectionModel().selectFirst();
        operator.setItems(operators);
        operator.setVisibleRowCount(3);
        operator.getSelectionModel().selectFirst();
    }
    
    private void setNonForwardDelay(){
        nonForwardDelay = (forwarding ? 0: 2);
    }
    
    private void initOperators(){
        fp_add = new Operator("fp_add",1,"fp_add_sub_unit","+|- (f)");
        fp_sub = new Operator("fp_sub",1,"fp_add_sub_unit","+|- (f)");
        fp_mult = new Operator("fp_mult",1,"fp_mult_unit","* (f)");
        fp_div = new Operator("fp_div",1,"fp_div_unit","/ (p)");
        fp_ld = new Operator("fp_ld",1,"int_unit","EX");
        fp_sd = new Operator("fp_sd",1,"int_unit","EX");
        int_add  = new Operator("int_add",1,"int_unit","+|- (i)");
        int_sub  = new Operator("int_sub",1,"int_unit","+|- (i)");
        int_mult = new Operator("int_mult",1,"int_mult_unit","* (i)");
        int_div  = new Operator("int_div",1,"int_div_unit","/ (i)");
        int_ld  = new Operator("int_ld",1,"int_unit","EX");
        int_sd  = new Operator("int_sd",1,"int_unit","EX");
        br_taken = new Operator("br_taken",1,"br_add"," ");
        br_untaken = new Operator("br_untaken",1,"br_add"," ");
    }
    
    public void insertInstruction(){
        Instruction newInstruction;
        
        String op = operator.getValue().toString();
        String destReg = destination_register.getValue().toString();
        String srcReg1 = source_register1.getValue().toString();
        String srcReg2 = source_register2.getValue().toString();
        
        if ("fp_sd".equals(op) || "int_sd".equals(op)){
            newInstruction = new Instruction("", destReg, srcReg2, null ,
                    getOperatorByString(operator.getValue().toString()));
	}
        else {
            newInstruction = new Instruction("", srcReg1, srcReg2, destReg,
                    getOperatorByString(operator.getValue().toString()));
	}
        
        instructions.add(newInstruction);
        ++numOfInstructions;
        instructions_table.addRow(numOfInstructions);
        Text instructionText = new Text(newInstruction.getInstructionData());
        instructions_table.add(instructionText, 0, numOfInstructions);
        currentCycle = 0;
        setNumOfCol();
        buildPipeline();
    }
    
    public void removeInstruction(){
        if(!instructions.isEmpty()){
            instructions.remove(instructions.size() - 1);
            --numOfInstructions;
            currentCycle = 0;
            setNumOfCol();
            buildPipeline();
        }
    }
    
    private void initEventListeners(){
        enableForwarding.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            forwarding = newValue;
            setNonForwardDelay();
            currentCycle = 0;
            setNumOfCol();
            buildPipeline();
        });
    }
    
    private Operator getOperatorByString(String operator){
        switch(operator){
            case "fp_add":
                return fp_add;
            case "fp_sub":
                return fp_sub;
            case"fp_mult":
                return fp_mult;
            case "fp_div":
                return fp_div;
            case "fp_ld":
                return fp_ld;
            case "fp_sd":
                return fp_sd;
            case "int_add":
                return int_add;
            case "int_sub":
                return int_sub;
            case "int_mult":
                return  int_mult;
            case "int_div":
                return int_div;
            case "int_ld":
                return int_ld;
            case "int_sd":
                return int_sd;
            case "br_taken":
                return br_taken;
            case "br_untaken":
                return br_untaken;
            default:
                return null;
        }
    }
    
    public void step(){
	// Do not do anything if we have already gone through all of the cycles.
	if (numOfCol == currentCycle){
		return;
	}
        int stall, x;
	if (numOfInstructions != 1){
	// ****** Multiple instructions in the array ******
        stall = 0;
        x = 0;
        while (x < numOfInstructions){
            // Break out of the loop when we get to an instruction which hasn't been entered into the pipeline yet.
            if (instructions.get(x).issueCycle == null){
                break;
            }
            // If the stall variable is set, then we should stall and not do any work.
            if (stall == 1){
		//Display_Stall();
                displayCell(x + 1, currentCycle + 1, "S");
            }
            else {
		// Find what stage the current instruciton is in and do work accordingly.
		switch(instructions.get(x).piplineStage.toString()){
                    case "IF" :
                        // Set the new stage to ID
			instructions.get(x).piplineStage = "ID";
			break;
                    case "ID" :
			// Check if this instruction can be executed by comparing it against all previous instructions.
                    for (int i = (x - 1); i >= 0 ; i--){
                        // If a previous instruction is in the EX stage and both instructions use the same functional unit.
			if (instructions.get(i).piplineStage == "EX" && instructions.get(i).operator.functionalUnit == instructions.get(x).operator.functionalUnit){
                            // *** Structural Hazard ***
                            stall = 1;
                            break;
			}
			// If the destination register of a previous instruciton is the same and one of the source 
			// registers of the current instruction.
			else if ((instructions.get(i).destinationRegister == instructions.get(x).sourceRegister1 || instructions.get(i).destinationRegister == instructions.get(x).sourceRegister2)){
			// We must check if data forwarding is enabled and handle that situation differently.
                            if (forwarding){
				// If forwarding is enabled and the previous instruction is a store or load
				// and is not in the WB stage or further in the pipeline, stall.
				if (instructions.get(i).operator.name == "fp_ld" || instructions.get(i).operator.name == "fp_sd"){
                                    if (instructions.get(i).piplineStage != "WB" && instructions.get(i).piplineStage != " "){
                                        // ** RAW Hazard **
					stall = 1;
					break;
                                    }
				}
				// If forwarding is enabled and the previous instruction is not a store or load
				// and is not in the MEM stage or further in the pipeline, stall.
				else {
                                    if (instructions.get(i).piplineStage != "MEM" && instructions.get(i).piplineStage != "WB" && instructions.get(i).piplineStage != " "){
                                        // *** RAW Hazard **
					stall = 1;
					break;
                                    }
				}
                            }
                            else if (!forwarding){
				// If forwarding is disabled and the previous instruction is not completed, stall.
				if (instructions.get(i).piplineStage != " "){
                                    // *** RAW Hazard **
                                    stall = 1;
                                    break;
				}
                            }
			}

			// If the destination registers are the same for both instructions and the destination registers
			// are not equal to "null."  The destination register will only equal "null" for BR and SD 
			// instructions.
			else if ((instructions.get(i).destinationRegister == instructions.get(x).destinationRegister) && (instructions.get(i).destinationRegister != null)){
                            // If a previous instruction is in the EX stage and the remaining cycles for that
                            // previous instruction is greater than or equal to the current instruction's 
                            // execution cycles minus one.  (The minus one is there because the previous instructions
                            // will have already been moved into their "next" stage while the current instruciton 
                            // hasn't been executed yet.)
                            if ((instructions.get(i).piplineStage == "EX") && ((instructions.get(i).operator.executionCycles - instructions.get(i).excuteCounter) >= (instructions.get(x).operator.executionCycles - 1))){
				// *** WAW Hazard ***
				stall = 1;
				break;
                            }

			}
			// If a previous instruction is in the EX stage and the remaining cycles for that
			// previous instruction is equal to the current instruction's execution cycles minus one.
			// (The minus one is there because the previous instructions will have already been moved into
			// their "next" stage while the current instruciton hasn't been executed yet.)
			else if ((instructions.get(i).piplineStage == "EX") && ((instructions.get(i).operator.executionCycles - instructions.get(i).excuteCounter) == (instructions.get(x).operator.executionCycles - 1))){					
			// *** WB will happen at the same time ***
                            stall = 1;
                            break;
			}
		} 	
		// If no stalls have been detected.
		if (stall != 1){
                    // If branch is taken, cancel following instruction and complete the execution of the branch.
                    if (instructions.get(x).operator.name == "br_taken"){
			instructions.get(x).piplineStage = " ";
			instructions.get(x).excuteCounter++;
			if ((x+1) < numOfInstructions){
                            instructions.get(x+1).piplineStage = " ";
			}
                    }
                    // Complete the execution of the branch.
                    else if (instructions.get(x).operator.name == "br_untaken"){
			instructions.get(x).piplineStage = " ";
			instructions.get(x).excuteCounter++;
                    }
                    // Move the instruction into the EX stage.
                    else {
			instructions.get(x).piplineStage = "EX";
			instructions.get(x).excuteCounter++;
                    }
		}
                    break;
                    case "EX" :
			// If we have completed execution of the current instruction.
			if (instructions.get(x).excuteCounter < instructions.get(x).operator.executionCycles){
                            instructions.get(x).piplineStage = "EX";
                            instructions.get(x).excuteCounter++;
			}
			// Move the instruction on to the next stage. 
			else{
                            instructions.get(x).piplineStage = "MEM";
			}
			break;
                    case "MEM" :
			instructions.get(x).piplineStage = "WB";
			break;
                    case "WB" :
			// Complete the execution of this instruction.
			instructions.get(x).piplineStage = " ";
			break;
                    case " " :
                    // Instructions that have completed, stay completed.
			instructions.get(x).piplineStage = " ";
			break;
                    default :
			// Handle the unlikely error that the instruction is in an undefined pipeline stage.
			return;
                }
		// If a stall occured, set the output to "s".
		if (stall == 1){
                    //Display_Stall();
                    displayCell(x + 1, currentCycle + 1, "S");
		}
		// If an instruction is in the EX stage, output the functional unit.
		else if (instructions.get(x).piplineStage == "EX"){
                    displayCell(x + 1, currentCycle + 1, instructions.get(x).operator.displayValue);
		}
		// Output the pipeline stage.
		else {
                    displayCell(x + 1, currentCycle + 1, (String) instructions.get(x).piplineStage);
		}
            }
            x++;
	} // End of while loop
        // If we didn't encounter a stall in one of the previous instructions, and if not all of the instructions are in the pipeline.
	if (stall != 1 && x < numOfInstructions){
            // Issue a new instruction.
            instructions.get(x).issueCycle = currentCycle;
            instructions.get(x).piplineStage = "IF";
            displayCell(x + 1, currentCycle + 1, (String) instructions.get(x).piplineStage);
	}

            // Increment the cycle count.
            currentCycle++;
    }
    else {
	// ******  Only one instruction in the array ******
	// Since there is only one instruction, no stalls can occur.		
	// If the current cycle is 0, we have to issue the only instruction.
	if (currentCycle == 0){
            instructions.get(0).issueCycle = currentCycle;
            instructions.get(0).piplineStage = "IF";
            displayCell(1, currentCycle + 1, (String) instructions.get(0).piplineStage);
            currentCycle++;
	}
	else{
            switch(instructions.get(0).piplineStage.toString()){
		case "IF" :
                    instructions.get(0).piplineStage = "ID";
                    break;
		case "ID" :
		// If branch is taken, complete this instruction.
		if (instructions.get(0).operator.name.substring(0,2) == "br"){
                    instructions.get(0).piplineStage = " ";
                    instructions.get(0).excuteCounter++;
		}	
		// Move the instruction into the EX stage.	
		else {
                    instructions.get(0).piplineStage = "EX";
                    instructions.get(0).excuteCounter++;
		}
                break;
		case "EX" :
                    // If the instruction hasn't completed.
                    if (instructions.get(0).excuteCounter < instructions.get(0).operator.executionCycles){
			instructions.get(0).piplineStage = "EX";
			instructions.get(0).excuteCounter++;
                    }
                    // Move the instruction to the MEM stage.
                    else{
			instructions.get(0).piplineStage = "MEM";
                    }
                    break;
		case "MEM" :
                    instructions.get(0).piplineStage = "WB";
                    break;
		case "WB" :
                    instructions.get(0).piplineStage = " ";
                    break;
		case " " :
                    instructions.get(0).piplineStage = " ";
                    break;
		default :
            }
                // If the instruction is in the EX stage, display the functional unit.
                if (instructions.get(0).piplineStage == "EX"){
                    displayCell(1, currentCycle + 1, instructions.get(0).operator.displayValue);
                }
                else {
                    displayCell(1, currentCycle + 1, (String) instructions.get(0).piplineStage);
                }
		currentCycle++;
            }
        }
    }

    private void displayCell(int row, int col, String value){
        Text text = new Text(value);
        instructions_table.add(text, col, row);
    }
    
    public void changeFpAddSubExCycles(){
        fp_add.changeExecutionTime((int) cfp_add_sub.getValue());
        fp_sub.changeExecutionTime((int) cfp_add_sub.getValue());
        setNumOfCol();
    }
    
    public void changeFpMulExCycles(){
        fp_mult.changeExecutionTime((int) cfp_mul.getValue());
        setNumOfCol();
    }
    
    public void changeFpDivExCycles(){
        fp_div.changeExecutionTime((int) cfp_div.getValue());
        setNumOfCol();
    }
    
    public void changeIntDivExCycles(){
        int_div.changeExecutionTime((int) cint_div.getValue());
        setNumOfCol();
    }
    
    public void changeIntAddSubExCycles(){
        int_add.changeExecutionTime((int) cint_add_sub.getValue());
        int_sub.changeExecutionTime((int) cint_add_sub.getValue());
        setNumOfCol();
    }
    
    public void changeIntMulExCycles(){
        int_mult.changeExecutionTime((int) cint_mul.getValue());
        setNumOfCol();
    }
    
    private void setNumOfCol(){
        numOfCol = 4 * instructions.size();
        for(int i = 0; i < instructions.size(); ++i){
            numOfCol += instructions.get(i).operator.executionCycles;
        }
    }
    
    private void buildPipeline(){
        instructions_table.getChildren().clear();
        if(numOfCol > 0){
            /*for(int i = 0; i <= numOfCol; ++i){
                ColumnConstraints col = new ColumnConstraints();
                col.setHgrow(Priority.ALWAYS );
                instructions_table.getColumnConstraints().add(col);
            }*/
            Text text = new Text("Instruction");
            text.textAlignmentProperty().set(TextAlignment.CENTER);
            //instructions_table.addRow(0);
            instructions_table.add(text, 0, 0);
            Text any;
            for(int i = 1; i < numOfCol; ++i){
                any = new Text(String.valueOf(i));
                any.textAlignmentProperty().set(TextAlignment.CENTER);
                instructions_table.add(any, i, 0);
            }
            for(int i = 0; i < numOfInstructions; ++i){
                any = new Text(instructions.get(i).getInstructionData());
                any.textAlignmentProperty().set(TextAlignment.CENTER);
                //instructions_table.addRow(i + 1);
                instructions_table.add(any, 0, i + 1);
            }
        }
    }
    
    public void stepToEnd(){
        for(int i = 0; i < numOfCol; ++i){
            step();
        }
    }
    
    public void reset(){
        forwarding = false;
        enableForwarding.setSelected(false);
        instructions.clear();
        numOfInstructions = 0;
        currentCycle = 0;
        setNumOfCol();
        buildPipeline();
    }
    
    private void loadOffsetRegister(ComboBox register){
        ObservableList<String> list = FXCollections.observableArrayList("Offset");
        register.setItems(list);
        register.disableProperty().set(true);
    }
    
    private void clearRegister(ComboBox register){
        register.getItems().clear();
        register.disableProperty().set(true);
    }
    
}
