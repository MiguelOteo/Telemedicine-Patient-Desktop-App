package models;

import java.util.LinkedList;
import java.util.List;

public class Patient extends User {
	
	private int patientId;
	
	// Id of the patient (Documentation)
	private String patientIdNumber;
	
	private float patientWeight;
	
	//List of the packages with BITalino measures
	private List<BitalinoPackage> measuredPackages;
	
	// Default constructor
	public Patient() {}

	// Constructor
	public Patient(int userId, int patientId, String patientName, String patientEmail, float patientWeight, String patientEncryptedPassword, String userSalt, String patientIdNumber) {
		
		super(userId, patientName, patientEmail, patientEncryptedPassword, userSalt);
		
		this.patientId = patientId;
		this.patientIdNumber = patientIdNumber;
		this.measuredPackages = new LinkedList<BitalinoPackage>();
	}
	
	public Patient(int patientId, String patientIdNumber, User user, float patientWeight) {
		super(user.getUserId(), user.getName(), user.getEmail(), user.getEncryptedPassword(), user.getUserSalt());
		
		this.patientId = patientId;
		this.patientIdNumber = patientIdNumber;
		this.measuredPackages = new LinkedList<BitalinoPackage>();
		this.patientWeight = patientWeight;
	}
	
	public void addNewPackage(BitalinoPackage recordPackage) {
		this.measuredPackages.add(recordPackage);
	}

	// Getter and Setter methods
	public int getPatientId() {return patientId;}

	public void setPatientId(int patientId) {this.patientId = patientId;}
	
	public String getPatientIdNumber() {return patientIdNumber;}
	
	public void setPatientIdNumber(String patientIdNumber) {this.patientIdNumber = patientIdNumber;}

	public List<BitalinoPackage> getMeasuredPackages() {return this.measuredPackages;}

	public void setMeasuredPackages(List<BitalinoPackage> measuredPackages) {this.measuredPackages = measuredPackages;}

	public float getPatientWeight() {return patientWeight;}

	public void setPatientWeight(float patientWeight) {this.patientWeight = patientWeight;}
}
