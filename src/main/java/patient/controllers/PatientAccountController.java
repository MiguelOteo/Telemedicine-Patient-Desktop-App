package patient.controllers;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

import static patient.params.PatientParams.*;

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
	private Label userHeightLabel;
	@FXML
	private JFXButton updateEmail;
	@FXML
	private JFXButton updateName;
	@FXML
	private JFXButton updateWeight;
	@FXML
	private JFXButton updateHeight;
	@FXML
	private JFXTextField userNameField;
	@FXML
	private JFXTextField userEmailField;
	@FXML
	private JFXTextField userWeightField;
	@FXML
	private JFXTextField userHeightField;
	@FXML
	private JFXPasswordField userOldPassword;
	@FXML
	private JFXPasswordField userNewPassword;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		userNameLabel.setText("Name: " + AccountObjectCommunication.getPatient().getName());
		userEmailLabel.setText("User Email: " + AccountObjectCommunication.getPatient().getEmail());
		userIDLabel.setText("Patient ID: " + AccountObjectCommunication.getPatient().getPatientIdNumber());
		userWeightLabel.setText("Weight: " + AccountObjectCommunication.getPatient().getPatientWeight() + " Kg");
		userHeightLabel.setText("Height: " + AccountObjectCommunication.getPatient().getPatientHeight() + " cm");
		
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
		
		RegexValidator validator2 = new RegexValidator();
		validator2.setRegexPattern("[1-9]{1}[0-9]{1,2}(.[0-9]){0,1}");
		validator2.setMessage("User weight is not valid");
		userWeightField.getValidators().add(validator2);
		userWeightField.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				userWeightField.validate();
			}
		});
		
		RegexValidator validator3 = new RegexValidator();
		validator3.setRegexPattern("[1-9]{1}[0-9]{1,2}");
		validator3.setMessage("User height is not valid");
		userHeightField.getValidators().add(validator3);
		userHeightField.focusedProperty().addListener((o, oldVal, newVal) ->{
			if(!newVal) {
				userHeightField.validate();
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
				updatePatientAccount(true);
			}
		});
		
		updateHeight.setOnAction((ActionEvent event) -> {
			if(userHeightField.validate()) {
				updatePatientAccount(false);
			}
		});
	}
	
	@FXML
	private void changePassword() {

		boolean resultNew = userNewPassword.validate();
		boolean resultOld = userOldPassword.validate();
		if(resultNew && resultOld) {
			changePasswordRequest();
		}
	}
	
	@FXML
	private void closeApp() {
		System.exit(0);
	}

	@FXML
	private void minWindow() {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
	}
	
	// Displays any error returned form the Rest API
	private void openDialog(String message) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(DIALOG_POP_UP_VIEW));
			Parent root = loader.load();
			DialogPopUpController controller = loader.getController();
			controller.setMessage(message);
			Stage stage = new Stage();
			stage.setHeight(130);
			stage.setWidth(300);
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Telelepsia Message");
			stage.getIcons().add(new Image(APP_ICON));
			
			// Set the pop-up in the center of the main menu window
			stage.setX(LogInController.getStage().getX() + LogInController.getStage().getWidth() / 2 - stage.getWidth() / 2);
			stage.setY(-75 + LogInController.getStage().getY() + LogInController.getStage().getHeight() / 2 - stage.getHeight() / 2);
			
			AccountObjectCommunication.getAnchorPane().setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> AccountObjectCommunication.getAnchorPane().setEffect(null));
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	

	private void updatePatientAccount(boolean weightUpdate) {
		
		Thread threadObject = new Thread("Updating Patient Account") {
			public void run() {
				try {
					
					HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/updatePatientData").openConnection();
					connection.setRequestMethod("POST");
					APIRequest requestAPI = new APIRequest();
					
					requestAPI.setPatientId(AccountObjectCommunication.getPatient().getPatientId());
					if(weightUpdate) {
						
						String userWeight = userWeightField.getText();
						if(!userWeight.equals("")) {requestAPI.setPatientWeight(Float.parseFloat(userWeight));}
						
					} else {
						
						String userHeight = userHeightField.getText();
						
						if(!userHeight.equals("")) {requestAPI.setPatientHeight(Float.parseFloat(userHeight));}
						
					}
					
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
					APIResponse responseAPI;
					responseAPI = new Gson().fromJson(response.toString(), APIResponse.class);

					if(!responseAPI.isError()) {	
						
						Platform.runLater(() -> {
							if(weightUpdate) {
								AccountObjectCommunication.getPatient().setPatientWeight(Float.parseFloat(userWeightField.getText()));
								userWeightLabel.setText("Weight: " + Float.parseFloat(userWeightField.getText()) + " Kg");
								userWeightField.setText("");
							} else {
								AccountObjectCommunication.getPatient().setPatientHeight(Float.parseFloat(userHeightField.getText()));
								userHeightLabel.setText("Height: " + Float.parseFloat(userHeightField.getText()) + " cm");
								userHeightField.setText("");
							}
							openDialog(responseAPI.getAPImessage());
						});
					} else {
						Platform.runLater(() -> {
							openDialog(responseAPI.getAPImessage());
							userHeightField.setText("");
							userWeightField.setText("");
						});
					}
					
					
				} catch (ConnectException | FileNotFoundException connectionError) {
					Platform.runLater(() -> {
						openDialog("Failed to connect to the server");
						userWeightField.setText("");
						userHeightField.setText("");
						});
				} catch (IOException error) {
					error.printStackTrace();
				}	
				
			}
		};
		threadObject.start();
	}
	
	
	private void updateAccount(boolean emailUpdate) {
		
		Thread threadObject = new Thread("Updating Account") {
			public void run() {
				try {
					
					HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/updateAccount").openConnection();
					connection.setRequestMethod("POST");
					APIRequest requestAPI = new APIRequest();
					
					requestAPI.setUserId(AccountObjectCommunication.getPatient().getUserId());
					if(emailUpdate) {
						
						String userEmail = userEmailField.getText();
						if(!userEmail.equals("")) {requestAPI.setUserEmail(userEmail);}
						
					} else {
						
						String userName = userNameField.getText();
						if(!userName.equals("")) {requestAPI.setUserName(userName);}
						
					}
					
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
					APIResponse responseAPI = new Gson().fromJson(response.toString(), APIResponse.class);
					
					if(!responseAPI.isError()) {	
						
						Platform.runLater(() -> {
							if(emailUpdate) {
								AccountObjectCommunication.getPatient().setEmail(responseAPI.getUserEmail());
								userEmailLabel.setText("Patient Email: " + responseAPI.getUserEmail());
								userEmailField.setText("");
							} else {
								AccountObjectCommunication.getPatient().setName(responseAPI.getUserName());
								userNameLabel.setText("Name: " +responseAPI.getUserName());
								userNameField.setText("");
							}
							openDialog(responseAPI.getAPImessage());
						});
					} else {
						Platform.runLater(() -> {
							openDialog(responseAPI.getAPImessage());
							userNameField.setText("");
							userEmailField.setText("");
						});
					}
					
				} catch (ConnectException | FileNotFoundException connectionError) {
					Platform.runLater(() -> {
						openDialog("Failed to connect to the server");
						userNameField.setText("");
						userEmailField.setText("");
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
					HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/changePassword").openConnection();
					connection.setRequestMethod("POST");
						
					String userNewPasswordString = userNewPassword.getText();
					String userOldPasswordString = userOldPassword.getText();
					
					APIRequest requestAPI = new APIRequest();
					requestAPI.setUserId(AccountObjectCommunication.getPatient().getUserId());
					if(!userOldPasswordString.equals("")) {requestAPI.setUserPassword(userOldPasswordString);}
					if(!userNewPasswordString.equals("")) {requestAPI.setUserNewPassword(userNewPasswordString);}
					
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
					APIResponse responseAPI;
					responseAPI = new Gson().fromJson(response.toString(), APIResponse.class);

					if(!responseAPI.isError()) {	
						AccountObjectCommunication.getPatient().setEncryptedPassword(responseAPI.getEncryptedPassword());
						Platform.runLater(() -> {
							openDialog(responseAPI.getAPImessage());
							userNewPassword.setText("");
							userOldPassword.setText("");
						});
					} else {
						Platform.runLater(() -> {
							openDialog(responseAPI.getAPImessage());
							userNewPassword.setText("");
							userOldPassword.setText("");
						});
					}
				} catch (ConnectException | FileNotFoundException connectionError) {
					Platform.runLater(() -> {
						openDialog("Failed to connect to the server");
						userNewPassword.setText("");
						userOldPassword.setText("");
					});
				} catch (IOException error) {
					error.printStackTrace();
				}
			}
		};
		threadObject.start();
	}
}
