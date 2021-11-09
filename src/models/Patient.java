package models;

import java.sql.Date;
import java.util.List;

public class Patient extends User {
	
	private int patientId;
	
	// Id of the patient (Documentation)
	private String patientIdNumber;
	
	//List of the dates he took measures with the BITalino
	private List<Date> measuresDates;
	
	// TODO - variables for the heart frequency and muscles activity
	
	// Default constructor
	public Patient() {}

	// Constructor
	public Patient(int userId, int patientId, String patientName, String patientEmail, String patientEncryptedPassword, String userSalt, String patientIdNumber) {
		
		super(userId, patientName, patientEmail, patientEncryptedPassword, userSalt);
		
		this.patientId = patientId;
		this.patientIdNumber = patientIdNumber;
		this.measuresDates = null;
	}
	
	public Patient(int patientId, String patientIdNumber, User user) {
		super(user.getUserId(), user.getName(), user.getEmail(), user.getEncryptedPassword(), user.getUserSalt());
		
		this.patientId = patientId;
		this.patientIdNumber = patientIdNumber;
		this.measuresDates = null;	
	}

	// Getter and Setter methods
	public int getPatientId() {return patientId;}

	public void setPatientId(int patientId) {this.patientId = patientId;}
	
	public String getPatientIdNumber() {return patientIdNumber;}
	
	public void setPatientIdNumber(String patientIdNumber) {this.patientIdNumber = patientIdNumber;}

	public List<Date> getMeasuresDates() {return measuresDates;}

	public void setMeasuresDates(List<Date> measuresDates) {this.measuresDates = measuresDates;}
}

