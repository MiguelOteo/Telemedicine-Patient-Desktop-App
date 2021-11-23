package doctorAccountPane;

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
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

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
import remoteParams.RestAPI;

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
		sendUpdateRequest(true);
	}
	
	@FXML
	private void changePassword() {
		sendUpdateRequest(false);
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
	
	private void sendUpdateRequest(boolean update) {
		
		Thread threadObject = new Thread("UpdatingDoctor") {
			public void run() {
				try {
					
					HttpURLConnection connection;
					APIRequest requestAPI = new APIRequest();
					
					if(update) {
						connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/doctorAccountUpdate").openConnection();
						connection.setRequestMethod("POST");
						
						String userNameString = userName.getText();
						String userEmailString = userEmail.getText();
						
						if(!userNameString.equals("")) {requestAPI.setUserName(userNameString);}
						if(!userEmailString.equals("")) {requestAPI.setUserEmail(userEmailString);}
						
					} else {
						connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/doctorChangePassword").openConnection();
						connection.setRequestMethod("POST");
						
						String userNewPasswordString = userNewPassword.getText();
						String userOldPasswordString = userOldPassword.getText();
						
						if(!userOldPasswordString.equals("")) {requestAPI.setUserPassword(userOldPasswordString);}
						if(!userNewPasswordString.equals("")) {requestAPI.setUserNewPassword(userNewPasswordString);}
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
					
					// TODO- Finish method
					System.out.println(responseAPI.getAPImessage());
					
				} catch (ConnectException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
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
