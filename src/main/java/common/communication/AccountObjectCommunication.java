package main.java.common.communication;

import com.jfoenix.controls.JFXButton;

import javafx.scene.layout.AnchorPane;
import main.java.common.models.Doctor;
import main.java.common.models.Patient;
import main.java.patient.controllers.ParametersRecordController;

public class AccountObjectCommunication {
	
	// To share the account on all the stages/panes etc
    private static Patient patient;
    private static Doctor doctor;
    private static int databaseId;
    private static String MAC = "";
    private static boolean isRecording = false;
    private static ParametersRecordController controller = null; 
   
	// To set the blur on the whole menu
    private static AnchorPane anchorPane;
    private static JFXButton buttonControl1;
   
    public static Patient getPatient() {return patient;}

    public static void setPatient(Patient passedPatient) {patient = passedPatient;}
    
    public static Doctor getDoctor() {return doctor;}
    
    public static void setDoctor(Doctor passedDoctor) {doctor = passedDoctor;}
    
	public static int getDatabaseId() {return databaseId;}

	public static void setDatabaseId(int id) {databaseId = id;}
    
    public static AnchorPane getAnchorPane() {return anchorPane;}
    
    public static void setAnchorPane(AnchorPane anchor) {anchorPane = anchor;}
    public static String getMAC() {return MAC;}

	public static void setMAC(String mAC) {MAC = mAC;}

	public static JFXButton getButtonControl1() {return buttonControl1;}

	public static void setButtonControl1(JFXButton buttonControl) {buttonControl1 = buttonControl;}

	public static boolean isRecording() {return isRecording;}

	public static void setRecording(boolean recording) {isRecording = recording;}

	public static ParametersRecordController getController() {return controller;}

	public static void setController(ParametersRecordController controllerObject) {controller = controllerObject;}
}