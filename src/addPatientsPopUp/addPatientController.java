package addPatientsPopUp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import communication.AccountObjectCommunication;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.APIRequest;
import models.APIResponse;
import models.Patient;
import remoteParams.RestAPI;

public class addPatientController implements Initializable {

	@FXML
	private JFXButton cancelOperation;
	@FXML
	private JFXButton addSelectedPatients;
	@FXML
	private JFXTreeTableView<PatientTreeObject> patientsTreeView;
	@FXML
	private final ObservableList<PatientTreeObject> patientsObjects = FXCollections.observableArrayList();
	
	// Stores the selected patients' ID when button "addSelected" is pressed
	private List<Integer> selectedPatients = new ArrayList<Integer>();
	
	// Stores all the APIResponse patients
	private List<Patient> patientsList;
	
	@Override 
	public void initialize(URL location, ResourceBundle resources) {		
		loadTreeTable();
		getPatients();
	}
	
	@FXML
	private void cancelOperation(MouseEvent event) throws IOException {
		Stage stage = (Stage) cancelOperation.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void addPatients(MouseEvent event) {
		
		for(PatientTreeObject patientObject: patientsObjects) {
			if(patientObject.selectedPatient.getValue().isSelected() == true) {
				selectedPatients.add(patientObject.patientId);
			}
		}
		
		if(!patientsList.isEmpty()) {
			addPatients();
		}
	}
	
	/*
	 *  --> HTTP requests methods
	 */
	
	// Sends a HTTP request to all selected patients and returns a new list of patients without a assigned doctor
	private void addPatients() {
		Thread threadObject = new Thread("AddingPatients") {
			public void run() {
				// TODO- HTTP request
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/addPatientsToDoctor")
							.openConnection();
					
					connection.setRequestMethod("POST");
					
					Gson gsonConverter = new Gson();
					APIRequest requestAPI = new APIRequest();
					requestAPI.setDoctorId(AccountObjectCommunication.getDoctor().getDoctorId());
					requestAPI.setSelectedPatients(selectedPatients);
					
					String postData = "APIRequest=" + URLEncoder.encode(gsonConverter.toJson(requestAPI), "UTF-8");
					
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(postData);
					writer.flush();
					
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = inputReader.readLine()) != null) {
						response.append(inputLine);
					}
					inputReader.close();

					APIResponse responseAPI = gsonConverter.fromJson(response.toString(), APIResponse.class);
					patientsList = responseAPI.getNoDoctorPatients();
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							loadData();
							selectedPatients.clear();
						}
					});
					
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							conncetionError.printStackTrace();
							//openDialog("Failed to connect to the server");
							selectedPatients.clear();
						}
					});
				} catch (IOException error) {
					error.printStackTrace();
					selectedPatients.clear();
				}
			}
		};
		threadObject.start();
	}
	
	// Sends a HTTP request to get all the patients without a assigned doctor
	private void getPatients() {
	
		Thread threadObject = new Thread("GettingPatients") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/listPatients")
							.openConnection();
					
					connection.setRequestMethod("POST");
					
					Gson gsonConverter = new Gson();
					APIRequest requestAPI = new APIRequest();
					requestAPI.setDoctorId(AccountObjectCommunication.getDoctor().getDoctorId());
					
					String postData = "APIRequest=" + URLEncoder.encode(gsonConverter.toJson(requestAPI), "UTF-8");

					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(postData);
					writer.flush();
					
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = inputReader.readLine()) != null) {
						response.append(inputLine);
					}
					inputReader.close();

					APIResponse responseAPI = gsonConverter.fromJson(response.toString(), APIResponse.class);
					patientsList = responseAPI.getNoDoctorPatients();
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							loadData();
						}
					});
					
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							conncetionError.printStackTrace();
							//openDialog("Failed to connect to the server");
						}
					});
				} catch (IOException error) {
					error.printStackTrace();
				}
			}
		};
		threadObject.start();
	}
	
	/*
	 * 	--> Tree Table View view
	 */
	
	// Reloads the list after some patients have been added to the doctor
	private void loadData() {
		
		patientsObjects.clear();
		for (Patient patient: patientsList) {
			patientsObjects.add(new PatientTreeObject(patient.getPatientId(), patient.getName(), patient.getPatientIdNumber()));
		}
		patientsTreeView.refresh();
	}
	
	// Establishes the list columns and loads the patients list
	private void loadTreeTable() {

		JFXTreeTableColumn<PatientTreeObject, String> patientName = new JFXTreeTableColumn<>("Patient Name");
		patientName.setPrefWidth(160);
		patientName.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PatientTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PatientTreeObject, String> param) {
				return param.getValue().getValue().patientName;
			}
		});
		patientName.setResizable(false);
		
		JFXTreeTableColumn<PatientTreeObject, String> patientIdNumber = new JFXTreeTableColumn<>("Patient ID Number");
		patientIdNumber.setPrefWidth(160);
		patientIdNumber.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PatientTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PatientTreeObject, String> param) {
				return param.getValue().getValue().patientIdNumber;
			}
		});
		patientIdNumber.setResizable(false);
		
		JFXTreeTableColumn<PatientTreeObject, JFXCheckBox> selectedPatients = new JFXTreeTableColumn<>("Select");
		selectedPatients.setPrefWidth(100);
		selectedPatients.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<PatientTreeObject, JFXCheckBox>, ObservableValue<JFXCheckBox>>() {
					@Override
					public ObservableValue<JFXCheckBox> call(CellDataFeatures<PatientTreeObject, JFXCheckBox> param) {
						return param.getValue().getValue().selectedPatient;
					}
				});
		selectedPatients.setResizable(false);
		
		TreeItem<PatientTreeObject> root = new RecursiveTreeItem<PatientTreeObject>(patientsObjects, RecursiveTreeObject::getChildren);
		patientsTreeView.setSelectionModel(null);
		patientsTreeView.setPlaceholder(new Label("No patients found without an assigned doctor"));
		patientsTreeView.getColumns().setAll(Arrays.asList(patientName, patientIdNumber, selectedPatients));
		patientsTreeView.setRoot(root);
		patientsTreeView.setShowRoot(false);
	}
}

class PatientTreeObject extends RecursiveTreeObject<PatientTreeObject> {
	
	int patientId;
	StringProperty patientName;
	StringProperty patientIdNumber;
	ObjectProperty<JFXCheckBox> selectedPatient;
	
	public PatientTreeObject(int patientId, String patientName, String patientIdNumber) {
		this.patientId = patientId;
		this.patientName = new SimpleStringProperty(patientName);
		this.patientIdNumber = new SimpleStringProperty(patientIdNumber);
		JFXCheckBox checkBox = new JFXCheckBox();
		checkBox.setCheckedColor(Color.web("#4f90a5",1.0));
		this.selectedPatient = new SimpleObjectProperty<JFXCheckBox>(checkBox);	
	}
}
