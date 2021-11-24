package doctorAccountPane;

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
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import commonParams.CommonParams;
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

public class DoctorAccountController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private Label userNameLabel;
	@FXML
	private Label userEmailLabel;
	@FXML
	private Label userDoctorIdLabel;
	@FXML
	private JFXTextField userName;
	@FXML
	private JFXTextField userEmail;
	@FXML
	private JFXPasswordField userOldPassword;
	@FXML
	private JFXPasswordField userNewPassword;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		userNameLabel.setText("Name: " + AccountObjectCommunication.getDoctor().getName());
		userEmailLabel.setText("Doctor Email: " + AccountObjectCommunication.getDoctor().getEmail());
		userDoctorIdLabel.setText("Doctor Identification: " + AccountObjectCommunication.getDoctor().getDoctorIdNumber());
	}
	
	@FXML
	private void updateAccount() {
		// TODO - Update Account Request
	}
	
	@FXML
	private void changePassword() {
		changePasswordRequest();
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
	
	private void changePasswordRequest() {
		
		Thread threadObject = new Thread("ChangingPassword") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/changePassword").openConnection();
					connection.setRequestMethod("POST");
						
					String userNewPasswordString = userNewPassword.getText();
					String userOldPasswordString = userOldPassword.getText();
					
					APIRequest requestAPI = new APIRequest();
					requestAPI.setUserId(AccountObjectCommunication.getDoctor().getUserId());
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
						AccountObjectCommunication.getDoctor().setEncryptedPassword(responseAPI.getEncryptedPassword());
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
