package treeTableObjects;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class PatientTreeObject extends RecursiveTreeObject<PatientTreeObject> {
	
	private Pane mainPane;
	
	private int patientId;
	private StringProperty patientName;
	private StringProperty patientEmail;
	private StringProperty patientIdNumber;
	private ObjectProperty<JFXButton> showBITalinoRecords;
	private ObjectProperty<JFXButton> unsignPatient;

	public PatientTreeObject(int patientId, String patientName, String patientEmail, String patientIdNumber, Pane pane) {
		
		this.mainPane = pane;
		
		this.patientId = patientId;
		this.patientName = new SimpleStringProperty(patientName);
		this.patientEmail = new SimpleStringProperty(patientEmail);
		this.patientIdNumber = new SimpleStringProperty(patientIdNumber);
		
		JFXButton showDetails = new JFXButton("Show details");
		showDetails.getStyleClass().add("table_button");
		showDetails.setOnAction((ActionEvent event) -> {
			openPatientRecords();
		});
		
		JFXButton deleteAsingment = new JFXButton("Delete assignment"); 
		deleteAsingment.getStyleClass().add("table_button");
		deleteAsingment.setOnAction((ActionEvent event) -> {
			deletePatient();
		});
		
		this.showBITalinoRecords = new SimpleObjectProperty<JFXButton>(showDetails);	
		this.unsignPatient = new SimpleObjectProperty<JFXButton>(deleteAsingment);
	}
	
	private void openPatientRecords() {
		Pane patientRecordsPane;
		try {
			patientRecordsPane = FXMLLoader.load(getClass().getResource("/patientRecordsPane/PatientRecordsLayout.fxml"));
			mainPane.getChildren().removeAll();
			mainPane.getChildren().setAll(patientRecordsPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	private void deletePatient() {
		// TODO - Add button function
	}
	
	// Getters methods
	public int getPatientId() {return patientId;}

	public StringProperty getPatientName() {return patientName;}

	public StringProperty getPatientEmail() {return patientEmail;}

	public StringProperty getPatientIdNumber() {return patientIdNumber;}

	public ObjectProperty<JFXButton> getShowBITalinoRecords() {return showBITalinoRecords;}

	public ObjectProperty<JFXButton> getUnsignPatient() {return unsignPatient;}
}