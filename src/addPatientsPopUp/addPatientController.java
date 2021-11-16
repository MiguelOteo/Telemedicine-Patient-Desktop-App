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
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import communication.AccountObjectCommunication;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.APIResponse;
import models.Patient;
import remoteParams.RestAPI;

public class addPatientController implements Initializable {

	@FXML
	private JFXButton cancelOperation;
	@FXML
	private JFXButton addSelectedPatients;
	@FXML
	private JFXTreeTableView<PatientTreeObject> PatientTreeView;
	@FXML
	private final ObservableList<PatientTreeObject> patientsObjects = FXCollections.observableArrayList();
	
	private List<Patient> patientsList;
	
	@Override 
	public void initialize(URL location, ResourceBundle resources) {		
		getPatients();
	}
	
	@FXML
	private void cancelOperation(MouseEvent event) throws IOException {
		Stage stage = (Stage) cancelOperation.getScene().getWindow();
		stage.close();
	}
	
	private void getPatients() {
	
		Thread threadObject = new Thread("GettingPatients") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/listPatients")
							.openConnection();
					
					connection.setRequestMethod("POST");
					
					String postData = "doctorId=" + URLEncoder.encode(Integer.toString(AccountObjectCommunication.getDoctor().getDoctorId()), "UTF-8");

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

					Gson gsonConverter = new Gson();
					APIResponse responseAPI = gsonConverter.fromJson(response.toString(), APIResponse.class);
					patientsList = responseAPI.getNoDoctorPatients();
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							setList();
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
	
	@SuppressWarnings("unchecked")
	private void setList() {
		
		JFXTreeTableColumn<PatientTreeObject, String> patientName = new JFXTreeTableColumn<>("Patient name");
		patientName.setPrefWidth(150);
		patientName.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PatientTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PatientTreeObject, String> param) {
				return param.getValue().getValue().patientName;
			}
		});
		patientName.setResizable(false);
		
		JFXTreeTableColumn<PatientTreeObject, String> patientIdNumber = new JFXTreeTableColumn<>("Patient ID number");
		patientIdNumber.setPrefWidth(150);
		patientIdNumber.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PatientTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PatientTreeObject, String> param) {
				return param.getValue().getValue().patientIdNumber;
			}
		});
		patientIdNumber.setResizable(false);
		
		JFXTreeTableColumn<PatientTreeObject, Boolean> selectedPatients = new JFXTreeTableColumn<>("Patient Selected");
		selectedPatients.setPrefWidth(150);
		selectedPatients.setCellFactory(new Callback<TreeTableColumn<PatientTreeObject ,Boolean>,TreeTableCell<PatientTreeObject,Boolean>>() {
		    @Override 
		    public TreeTableCell<PatientTreeObject, Boolean> call( TreeTableColumn<PatientTreeObject,Boolean> param) {
		        CheckBoxTreeTableCell<PatientTreeObject,Boolean> cell = new CheckBoxTreeTableCell<PatientTreeObject,Boolean>();
		        cell.setAlignment(Pos.CENTER);
		        return cell;
		    }
		});
		selectedPatients.setResizable(false);
		
		
		for (Patient patient: patientsList) {
				patientsObjects.add(new PatientTreeObject(patient.getName(), patient.getPatientIdNumber(), false));
		}
		
		TreeItem<PatientTreeObject> root = new RecursiveTreeItem<PatientTreeObject>(patientsObjects, RecursiveTreeObject::getChildren);
		PatientTreeView.setPlaceholder(new Label("No patients found"));
		PatientTreeView.getColumns().setAll(patientName, patientIdNumber, selectedPatients);
		PatientTreeView.setRoot(root);
		PatientTreeView.setShowRoot(false);
	}
}

class PatientTreeObject extends RecursiveTreeObject<PatientTreeObject> {
	
	StringProperty patientName;
	StringProperty patientIdNumber;
	BooleanProperty selectedPatient;
	
	public PatientTreeObject(String patientName, String patientIdNumber, boolean selected) {
		this.patientName = new SimpleStringProperty(patientName);
		this.patientIdNumber = new SimpleStringProperty(patientIdNumber);
		this.selectedPatient = new SimpleBooleanProperty(selected);	
	}
}
