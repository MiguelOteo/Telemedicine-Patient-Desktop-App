package patientRecordsPane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import com.google.gson.Gson;

import communication.AccountObjectCommunication;
import dialogPopUp.DialogPopUpController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.APIRequest;
import models.APIResponse;
import models.Patient;
import remoteParams.RestAPI;

public class PatientRecordsController implements Initializable {

	private Patient patient = new Patient();
	
	@FXML
	private Pane mainPane;
	@FXML
	private Label patientName;
	@FXML
	private Label patientEmail;
	@FXML
	private Label patientIdNumber;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		this.patient.setPatientId(AccountObjectCommunication.getDatabaseId());
		getPatientInformation();
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
	
	@FXML
	private void goBack(MouseEvent event) {
		Pane doctorPatientsPane;
		try {
			doctorPatientsPane = FXMLLoader.load(getClass().getResource("/doctorPatientsPane/DoctorPatientsLayout.fxml"));
			mainPane.getChildren().removeAll();
			mainPane.getChildren().setAll(doctorPatientsPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	private void loadInformation() {
		patientName.setText("Name: " + patient.getName());
		patientIdNumber.setText("Patient ID: " + patient.getPatientIdNumber());
		patientEmail.setText("Patient email: " + patient.getEmail());
	}
	
	// Displays any error returned form the Rest API
	private void openDialog(String message) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialogPopUp/dialogPopUpLayout.fxml"));
			Parent root = (Parent) loader.load();
			DialogPopUpController controler = loader.getController();
			controler.setMessage(message);
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
			});
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	private void getPatientInformation() {
		Thread threadObject = new Thread("gettingPatientInfo") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/getPatientInformation")
							.openConnection();

					connection.setRequestMethod("POST");
					
					APIRequest requestAPI = new APIRequest();
					requestAPI.setPatientId(patient.getPatientId());
					String postData = "APIRequest=" + URLEncoder.encode(new Gson().toJson(requestAPI), "UTF-8");
					
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
					
					if (!responseAPI.isError()) {
						patient = responseAPI.getPatient();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								loadInformation();
							}
						});
						
					} else {
						openDialog(responseAPI.getAPImessage());
					}
					
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							conncetionError.printStackTrace();
							openDialog("Failed to connect to the server");
						}
					});
				} catch (IOException error) {
					error.printStackTrace();
				}
			}
		};
		threadObject.start();
	}
}
