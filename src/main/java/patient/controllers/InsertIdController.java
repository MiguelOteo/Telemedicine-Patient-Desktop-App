package patient.controllers;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import patient.communication.AccountObjectCommunication;
import patient.models.APIRequest;
import patient.models.APIResponse;
import patient.utility.RegexValidator;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static patient.params.PatientParams.BASE_URL;
import static patient.params.PatientParams.DNI_LETTERS;

public class InsertIdController implements Initializable {

	@FXML
	JFXButton confirmButton;
	@FXML
	JFXTextField idField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		RegexValidator validator = new RegexValidator();
		validator.setRegexPattern("[0-9]{8}" + DNI_LETTERS + "{1}");
		
		idField.setPromptText("Health insurance number");
		validator.setMessage("Patient ID Number is not valid");
		
		idField.getValidators().add(validator);
		idField.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				idField.validate();
			}
		});
		
		confirmButton.setOnAction((ActionEvent event) -> {
			if(idField.validate()) {
				confirmButton.setDisable(true);
				updateId(idField.getText());
			}
		});
	}
	
	private void updateId(String idValue) {
		
		Thread threadObject = new Thread("InsertingIdNumber") {
			public void run() {
				
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/addPatientIdNumber").openConnection();
					connection.setRequestMethod("POST");
					
					APIRequest requestAPI = new APIRequest();
					if(!idValue.equals("")) {requestAPI.setPatientIdNumber(idValue);}
					requestAPI.setPatientId(AccountObjectCommunication.getPatient().getPatientId());
					String postData = "APIRequest=" + URLEncoder.encode(new Gson().toJson(requestAPI), StandardCharsets.UTF_8);
					
					
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(postData);
					writer.flush();
					
					BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuilder response = new StringBuilder();
					while ((inputLine = inputReader.readLine()) != null) {
						response.append(inputLine);
					}
					inputReader.close();

					Gson gsonConverter = new Gson();
					APIResponse responseAPI = gsonConverter.fromJson(response.toString(), APIResponse.class);
					
					if(responseAPI.isError()) {
						Platform.runLater(() -> {
							confirmButton.setDisable(false);
							idField.setText("");
						});
						
					} else {
					
						AccountObjectCommunication.getPatient().setPatientIdNumber(idValue);
						
						Platform.runLater(() -> {
							Stage stage = (Stage) confirmButton.getScene().getWindow();
							stage.close();
						});
					}
					
				} catch (ConnectException | FileNotFoundException connectionError) {
					Platform.runLater(() -> {
						// TODO - Show error message
					});
				} catch (IOException error) {
					error.printStackTrace();
				}
			}
		};
		threadObject.start();
	}
	

}
