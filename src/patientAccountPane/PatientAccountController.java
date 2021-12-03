package patientAccountPane;

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
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import commonParams.CommonParams;
import communication.AccountObjectCommunication;
import dialogPopUp.DialogPopUpController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import utility.RegexValidator;

public class PatientAccountController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private Label userNameLabel;
	@FXML
	private Label userEmailLabel;
	@FXML
	private Label userIDLabel;
	@FXML
	private Label userWeightLabel;
	@FXML
	private JFXButton updateEmail;
	@FXML
	private JFXButton updateName;
	@FXML
	private JFXButton updateWeight;
	@FXML
	private JFXTextField userNameField;
	@FXML
	private JFXTextField userEmailField;
	@FXML
	private JFXTextField userWeightField;
	@FXML
	private JFXPasswordField userOldPassword;
	@FXML
	private JFXPasswordField userNewPassword;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		userNameLabel.setText("Name: " + AccountObjectCommunication.getPatient().getName());
		userEmailLabel.setText("User Email: " + AccountObjectCommunication.getPatient().getEmail());
		userIDLabel.setText("Patient ID: " + AccountObjectCommunication.getPatient().getPatientIdNumber());
		userWeightLabel.setText("Weight: " + AccountObjectCommunication.getPatient().getPatientWeight());
		
		RegexValidator validator = new RegexValidator();
		validator.setRegexPattern("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		validator.setMessage("Email is not valid");
		userEmailField.getValidators().add(validator);
		userEmailField.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				userEmailField.validate();
			}
		});
		
		RequiredFieldValidator validatorEmpty3 = new RequiredFieldValidator(); 
		validatorEmpty3.setMessage("User name cannot be empty");
		userNameField.getValidators().add(validatorEmpty3);
		userNameField.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				userNameField.validate();
			}
		});
		
		RequiredFieldValidator validatorEmpty4 = new RequiredFieldValidator(); 
		validatorEmpty4.setMessage("User weight cannot be empty");
		userWeightField.getValidators().add(validatorEmpty4);
		userWeightField.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				userWeightField.validate();
			}
		});
		
		RequiredFieldValidator validatorEmpty = new RequiredFieldValidator();
		validatorEmpty.setMessage("Old password cannot be empty");
		userOldPassword.getValidators().add(validatorEmpty);
		userOldPassword.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				userOldPassword.validate();
			}
		});
		
		RequiredFieldValidator validatorEmpty2 = new RequiredFieldValidator();
		validatorEmpty2.setMessage("New password cannot be empty");
		userNewPassword.getValidators().add(validatorEmpty2);
		userNewPassword.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				userNewPassword.validate();
			}
		});
		
		updateEmail.setOnAction((ActionEvent event) -> {
			if(userEmailField.validate()) {
				updateAccount(true);
			}
		});
		
		updateName.setOnAction((ActionEvent event) -> {
			if(userNameField.validate()) {
				updateAccount(false);
			}
		});
		updateWeight.setOnAction((ActionEvent event) -> {
			if(userWeightField.validate()) {
				updateAccount(false);
			}
		});
	}
	
	@FXML
	private void changePassword() {

		boolean resultNew = userNewPassword.validate();
		boolean resultOld = userOldPassword.validate();
		if(resultNew == true && resultOld == true) {
			changePasswordRequest();
		}
	}
	
	@FXML
	private void closeApp(MouseEvent event) {
		System.exit(0);
	}

	@FXML
	private void minWindow(MouseEvent event) {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
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
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	private void updateAccount(boolean emailUpdate) {
		
		Thread threadObject = new Thread("Updating Account") {
			public void run() {
				try {
					
					HttpURLConnection connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/updateAccount").openConnection();
					connection.setRequestMethod("POST");
					APIRequest requestAPI = new APIRequest();
					
					requestAPI.setUserId(AccountObjectCommunication.getPatient().getUserId());
					if(emailUpdate) {
						
						String userEmail = userEmailField.getText();
						if(!userEmail.equals("")) {requestAPI.setUserEmail(userEmail);}
						
					} else {
						
						String userName = userNameField.getText();
						if(!userName.equals("")) {requestAPI.setUserName(userName);}
						String userWeight = userWeightField.getText();
						if(!userName.equals("")) {requestAPI.setUserName(userName);}
					}
					
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
					APIResponse responseAPI = new Gson().fromJson(response.toString(), APIResponse.class);
					
					if(!responseAPI.isError()) {	
						
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								if(emailUpdate) {
									AccountObjectCommunication.getPatient().setEmail(responseAPI.getUserEmail());
									userEmailLabel.setText("Patient Email: " + responseAPI.getUserEmail());
									userEmailField.setText("");
								} else {
									AccountObjectCommunication.getPatient().setName(responseAPI.getUserName());
									userNameLabel.setText("Name: " +responseAPI.getUserName());
									userNameField.setText("");
									//AccountObjectCommunication.getPatient().setWeight(responseAPI.getUserName());
									//userNameLabel.setText("Weight: " +responseAPI.getUserName());
									//userNameField.setText("");
								}
								openDialog(responseAPI.getAPImessage());
							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								openDialog(responseAPI.getAPImessage());
								userNameField.setText("");
								userEmailField.setText("");
							}
						});
					}
					
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							openDialog("Failed to connect to the server");
							userNameField.setText("");
							userEmailField.setText("");
						}
					});
				} catch (IOException error) {
					error.printStackTrace();
				}	
			}
		};
		threadObject.start();
	}
	
	private void changePasswordRequest() {
		
		Thread threadObject = new Thread("ChangingPassword") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/changePassword").openConnection();
					connection.setRequestMethod("POST");
						
					String userNewPasswordString = userNewPassword.getText();
					String userOldPasswordString = userOldPassword.getText();
					
					APIRequest requestAPI = new APIRequest();
					requestAPI.setUserId(AccountObjectCommunication.getPatient().getUserId());
					if(!userOldPasswordString.equals("")) {requestAPI.setUserPassword(userOldPasswordString);}
					if(!userNewPasswordString.equals("")) {requestAPI.setUserNewPassword(userNewPasswordString);}
					
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
					APIResponse responseAPI = new Gson().fromJson(response.toString(), APIResponse.class);
					
					if(!responseAPI.isError()) {	
						AccountObjectCommunication.getPatient().setEncryptedPassword(responseAPI.getEncryptedPassword());
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								openDialog(responseAPI.getAPImessage());
								userNewPassword.setText("");
								userOldPassword.setText("");
							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								openDialog(responseAPI.getAPImessage());
								userNewPassword.setText("");
								userOldPassword.setText("");
							}
						});
					}
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							openDialog("Failed to connect to the server");
							userNewPassword.setText("");
							userOldPassword.setText("");
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
