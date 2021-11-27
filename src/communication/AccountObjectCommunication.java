package communication;

import javafx.scene.layout.AnchorPane;
import models.Doctor;
import models.Patient;

public class AccountObjectCommunication {
	
	// To share the account on all the stages/panes etc
    private static Patient patient;
    private static Doctor doctor;
    private static int databaseId;
    private static String MAC;
   
   

	// To set the blur on the whole menu
    private static AnchorPane anchorPane;
   
    public static Patient getPatient() {
        return patient;
    }

    public static void setPatient(Patient passedPatient) {
    	patient = passedPatient;
    }
    
    public static Doctor getDoctor() {
    	return doctor;
    }
    
    public static void setDoctor(Doctor passedDoctor) {
    	doctor = passedDoctor;
	}
    
	public static int getDatabaseId() {
		return databaseId;
	}

	public static void setDatabaseId(int id) {
		databaseId = id;
	}
    
    public static AnchorPane getAnchorPane() {
    	return anchorPane;
    }
    
    public static void setAnchorPane(AnchorPane anchor) {
    	anchorPane = anchor;
    }
    public static String getMAC() {
		return MAC;
	}

	public static void setMAC(String mAC) {
		MAC = mAC;
	}
}