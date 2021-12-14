package main.java.common.controllers;

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
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import main.java.common.communication.AccountObjectCommunication;
import main.java.common.models.APIRequest;
import main.java.common.models.APIResponse;
import main.java.common.params.CommonParams;
import main.java.common.utility.RegexValidator;


public class InsertIdController implements Initializable {

	@FXML
	JFXButton confirmButton;
	@FXML
	JFXTextField idField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		RegexValidator validator = new RegexValidator();
		validator.setRegexPattern("[0-9]{8}" + CommonParams.DNI_LETTERS + "{1}");
		
		if(AccountObjectCommunication.getDoctor() != null) {
			idField.setPromptText("Doctor identification number");
			validator.setMessage("Doctor ID Number is not valid");
		} else {
			idField.setPromptText("Health insurance number");
			validator.setMessage("Patient ID Number is not valid");
		}
		
		idField.getValidators().add(validator);
		idField.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				idField.validate();
			}
		});
		
		confirmButton.setOnAction((ActionEvent event) -> {
			if(idField.validate()) {
				confirmButton.setDisable(true);
				updateId(idField.getText().toString());
			}
		});
	}
	
	private void updateId(String idValue) {
		
		Thread threadObject = new Thread("InsertingIdNumber") {
			public void run() {
				
				try {
					HttpURLConnection connection;
					String postData;
					
					if(AccountObjectCommunication.getDoctor() != null) {
			
						connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/addDoctorIdentification").openConnection();
						connection.setRequestMethod("POST");
						
						APIRequest requestAPI = new APIRequest();
						if(!idValue.equals("")) {requestAPI.setDoctorIdentification(idValue);}
						requestAPI.setDoctorId(AccountObjectCommunication.getDoctor().getDoctorId());
						postData = "APIRequest=" + URLEncoder.encode(new Gson().toJson(requestAPI), "UTF-8");
					} else {
						
						connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/addPatientIdNumber").openConnection();
						connection.setRequestMethod("POST");
						
						APIRequest requestAPI = new APIRequest();
						if(!idValue.equals("")) {requestAPI.setPatientIdNumber(idValue);}
						requestAPI.setPatientId(AccountObjectCommunication.getPatient().getPatientId());
						postData = "APIRequest=" + URLEncoder.encode(new Gson().toJson(requestAPI), "UTF-8");	
					}
					
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
					
					if(responseAPI.isError()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								confirmButton.setDisable(false);
								idField.setText("");
							}
						});
						
					} else {
						
						if(AccountObjectCommunication.getDoctor() != null) {
							AccountObjectCommunication.getDoctor().setDoctorIdNumber(idValue);
						} else {
							AccountObjectCommunication.getPatient().setPatientIdNumber(idValue);
						}
						
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								Stage stage = (Stage) confirmButton.getScene().getWindow();
								stage.close();
							}
						});
					}
					
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// TODO - Show error message
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
