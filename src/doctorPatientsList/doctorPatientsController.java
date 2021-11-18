package doctorPatientsList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import communication.AccountObjectCommunication;
import doctorMainMenu.DoctorMenuController;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import models.APIRequest;
import models.APIResponse;
import models.Patient;
import remoteParams.RestAPI;

@SuppressWarnings("all")
public class doctorPatientsController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private JFXButton addPatients;
	@FXML
	private JFXTreeTableView patientsTreeView;
	@FXML
	private final ObservableList<PatientTreeObject> patientsObjects = FXCollections.observableArrayList();
	
	// Stores all the APIResponse patients
	private List<Patient> patientsList;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadTreeTable();
		getDoctorPatients();
	}
	
	@FXML
	private void openAddPatients() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/addPatientsPopUp/addPatientsLayout.fxml"));
			Parent root = (Parent) loader.load();
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			AccountObjectCommunication.getAnchorPane().setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> {
				AccountObjectCommunication.getAnchorPane().setEffect(null);
				getDoctorPatients();
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	@FXML
	private void minWindow(MouseEvent event) {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
	}
	
	@FXML
	private void closeApp(MouseEvent event) {
		System.exit(0);
	}
	
	/*
	 *  --> HTTP requests methods
	 */
	
	// Sends a HTTP request to get all the patients with the assigned doctorId
	private void getDoctorPatients() {
		Thread threadObject = new Thread("GettingDoctorPatients") {
			public void run() {
				// TODO- HTTP request
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/listDoctorPatients")
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
	
	// Loads the list of patients assigned to the doctorId
	private void loadData() {
		
		patientsObjects.clear();
		for (Patient patient: patientsList) {
			patientsObjects.add(new PatientTreeObject(patient.getPatientId(), patient.getName(), patient.getEmail(), patient.getPatientIdNumber()));
		}
		patientsTreeView.refresh();
	}
	
	// Establishes the tree table view columns
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
		
		JFXTreeTableColumn<PatientTreeObject, String> patientEmail = new JFXTreeTableColumn<>("Patient Email");
		patientEmail.setPrefWidth(160);
		patientEmail.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PatientTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PatientTreeObject, String> param) {
				return param.getValue().getValue().patientEmail;
			}
		});
		patientEmail.setResizable(false);
		
		JFXTreeTableColumn<PatientTreeObject, String> patientIdNumber = new JFXTreeTableColumn<>("Patient ID Number");
		patientIdNumber.setPrefWidth(160);
		patientIdNumber.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PatientTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PatientTreeObject, String> param) {
				return param.getValue().getValue().patientIdNumber;
			}
		});
		patientIdNumber.setResizable(false);
		
		JFXTreeTableColumn<PatientTreeObject, JFXButton> showRecords = new JFXTreeTableColumn<>("Show Records");
		showRecords.setPrefWidth(100);
		showRecords.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<PatientTreeObject, JFXButton>, ObservableValue<JFXButton>>() {
			@Override
			public ObservableValue<JFXButton> call(CellDataFeatures<PatientTreeObject, JFXButton> param) {
				return param.getValue().getValue().showBITalinoRecords;
			}
		});
		showRecords.setResizable(false);
		
		JFXTreeTableColumn<PatientTreeObject, JFXButton> deletePatient = new JFXTreeTableColumn<>("Unassign patient");
		deletePatient.setPrefWidth(140);
		deletePatient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<PatientTreeObject, JFXButton>, ObservableValue<JFXButton>>() {
			@Override
			public ObservableValue<JFXButton> call(CellDataFeatures<PatientTreeObject, JFXButton> param) {
				return param.getValue().getValue().unsignPatient;
			}
		});
		deletePatient.setResizable(false);
		
		TreeItem<PatientTreeObject> root = new RecursiveTreeItem<PatientTreeObject>(patientsObjects, RecursiveTreeObject::getChildren);
		patientsTreeView.setSelectionModel(null);
		patientsTreeView.setPlaceholder(new Label("No patients found assigned to this account"));
		patientsTreeView.getColumns().setAll(Arrays.asList(patientName, patientEmail, patientIdNumber, showRecords, deletePatient));
		patientsTreeView.setRoot(root);
		patientsTreeView.setShowRoot(false);
	}
}

class PatientTreeObject extends RecursiveTreeObject<PatientTreeObject> {
	
	int patientId;
	StringProperty patientName;
	StringProperty patientEmail;
	StringProperty patientIdNumber;
	ObjectProperty<JFXButton> showBITalinoRecords;
	ObjectProperty<JFXButton> unsignPatient;
	
	public PatientTreeObject(int patientId, String patientName, String patientEmail, String patientIdNumber) {
		this.patientId = patientId;
		this.patientName = new SimpleStringProperty(patientName);
		this.patientEmail = new SimpleStringProperty(patientEmail);
		this.patientIdNumber = new SimpleStringProperty(patientIdNumber);
		
		// TODO - Add button function
		this.showBITalinoRecords = new SimpleObjectProperty<JFXButton>(new JFXButton("Show"));	
		this.unsignPatient = new SimpleObjectProperty<JFXButton>(new JFXButton("Delete"));
	}
}
