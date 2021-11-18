package logIn;

import dialogPopUp.DialogPopUpController;

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

import communication.AccountObjectCommunication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import launchApp.LaunchApp;
import models.APIRequest;
import models.APIResponse;
import remoteParams.RestAPI;

public class LogInController implements Initializable {

	// JavaFx layout elements
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private Pane logInPane;
	@FXML
	private JFXButton logInButton;
	@FXML
	private JFXButton signUpButton;
	@FXML
	private JFXTextField userEmailField;
	@FXML
	private JFXPasswordField userPasswordField;

	// Default constructor
	public LogInController() {}

	// Initialize method
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Initialize the logIn button on press event
		logInButton.setOnAction((ActionEvent event) -> {
			loginUserRest(userEmailField.getText(), userPasswordField.getText());
			logInButton.setDisable(true);
		});
	}

	// Replace the login pane with the registration one
	@FXML
	private void openRegistration(MouseEvent event) throws IOException {
		Pane registrationPane = FXMLLoader.load(getClass().getResource("/userRegistration/RegistrationLayout.fxml"));
		anchorPane.getChildren().remove(logInPane);
		anchorPane.getChildren().setAll(registrationPane);
	}

	@FXML
	private void closeApp(MouseEvent event) throws IOException {
		System.exit(0);
	}

	@FXML
	private void minWindow(MouseEvent event) {
		Stage stage = (Stage) logInPane.getScene().getWindow();
		stage.setIconified(true);
	}

	private void launchMenu(String fileName) {
		try {
			LaunchApp.getStage().hide();
			userEmailField.setText("");
			userPasswordField.setText("");
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName + ".fxml"));
			Parent root = (Parent) loader.load();
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setScene(scene);
			stage.show();
			stage.setOnHiding(event -> {
				logInButton.setDisable(false);
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

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
			anchorPane.setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> {
				anchorPane.setEffect(null);
				logInButton.setDisable(false);
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	private void loginUserRest(String userEmail, String userPassword) {

		Thread threadObject = new Thread("AthentificatingUser") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/userLogin")
							.openConnection();

					connection.setRequestMethod("POST");
					
					APIRequest requestAPI = new APIRequest();
					if(!userEmail.equals("")) {requestAPI.setUserEmail(userEmail);}
					if(!userPassword.equals("")) {requestAPI.setUserPassword(userPassword);}
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

					if (responseAPI.isError()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								openDialog(responseAPI.getAPImessage());
							}
						});
					} else {
						if (responseAPI.getPatient() != null) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									AccountObjectCommunication.setPatient(responseAPI.getPatient());
									launchMenu("/patientMainMenu/PatientMenuLayout");
								}
							});
						} else {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									AccountObjectCommunication.setDoctor(responseAPI.getDoctor());
									launchMenu("/doctorMainMenu/DoctorMenuLayout");
								}
							});
						}
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

// #00d4ff Original color
