package insertIdPopUp;

import java.io.BufferedReader;
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

import communication.AccountObjectCommunication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import models.APIResponse;
import remoteParams.RestAPI;


public class insertIdController implements Initializable {

	@FXML
	JFXButton confirmButton;
	@FXML
	JFXTextField idField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		confirmButton.setOnAction((ActionEvent event) -> {
			
			confirmButton.setDisable(true);
			updateId(idField.getText().toString());
		});
	}
	
	private void updateId(String idValue) {
		
		Thread threadObject = new Thread("InsertingIdNumber") {
			public void run() {
				
				try {
					HttpURLConnection connection;
					String postData;
					
					if(AccountObjectCommunication.getDoctor() != null) {
			
						connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/addDoctorAutentification").openConnection();
						connection.setRequestMethod("POST");
						
						postData = "doctorId=" + URLEncoder.encode(Integer.toString(AccountObjectCommunication.getDoctor().getDoctorId()), "UTF-8");
						postData += "&doctorIdentification=" + URLEncoder.encode(idValue, "UTF-8");
					} else {
						
						connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/addPatientIdNumber").openConnection();
						connection.setRequestMethod("POST");
						
						postData = "patientId=" + URLEncoder.encode(Integer.toString(AccountObjectCommunication.getPatient().getPatientId()), "UTF-8");
						postData += "&patientIdNumber=" + URLEncoder.encode(idValue, "UTF-8");
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
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								Stage stage = (Stage) confirmButton.getScene().getWindow();
								stage.close();
							}
						});
					}
					
				} catch (ConnectException conncetionError) {
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
