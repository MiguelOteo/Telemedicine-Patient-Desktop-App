package userRegistration;

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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import dialogPopUp.DialogPopUpController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import launchApp.LaunchApp;
import models.APIResponse;
import remoteParams.RestAPI;

public class RegistrationController implements Initializable {

	@FXML
	private Pane registrationPane;
	@FXML
	private JFXComboBox<String> userType;
	@FXML
	private JFXTextField userNameField;
	@FXML
	private JFXTextField userEmailField;
	@FXML
	private JFXPasswordField userPasswordField;
	@FXML
	private JFXPasswordField userRepeatPasswordField;
	@FXML
	private JFXButton registerButton;
	@FXML
	private JFXButton goBackButton;

	// Default constructor
	public RegistrationController() {}

	public void initialize(URL location, ResourceBundle resources) {

		// Adding the types of users
		userType.getItems().addAll("Patient", "Doctor");
		userType.setValue("Patient");

		registerButton.setOnAction((ActionEvent event) -> {
			resgisterUserRest(userType.getSelectionModel().getSelectedItem().toString(), userNameField.getText(),
					userEmailField.getText(), userPasswordField.getText(), userRepeatPasswordField.getText());
			registerButton.setDisable(true);
		});
	}

	// When press it goes back to the logIn pane
	@FXML
	private void backToMenu(MouseEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/logIn/LogInLayout.fxml"));
		LaunchApp.getStage().getScene().setRoot(root);
	}

	@FXML
	private void closeApp(MouseEvent event) {
		System.exit(0);
	}

	@FXML
	private void minWindow(MouseEvent event) {
		Stage stage = (Stage) registrationPane.getScene().getWindow();
		stage.setIconified(true);
	}

	private void openErrorDialog(String message) {
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
			registrationPane.getParent().setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> {
				registrationPane.getParent().setEffect(null);
				registerButton.setDisable(false);
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	private void openAccountCreatedDialog(String message) {
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
			registrationPane.getParent().setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> {
				registrationPane.getParent().setEffect(null);
				registerButton.setDisable(false);
				goBack();
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	private void goBack() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/logIn/LogInLayout.fxml"));
			LaunchApp.getStage().getScene().setRoot(root);
			registerButton.setDisable(false);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	private void resgisterUserRest(String userType, String userName, String userEmail, String userPassword,
			String userRepeatPassword) {

		Thread threadObject = new Thread("RegisteringUser") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/userRegistration")
							.openConnection();
					connection.setRequestMethod("POST");

					String postData = "userType=" + URLEncoder.encode(userType, "UTF-8");
					postData += "&userName=" + URLEncoder.encode(userName, "UTF-8");
					postData += "&userEmail=" + URLEncoder.encode(userEmail, "UTF-8");
					postData += "&userPassword=" + URLEncoder.encode(userPassword, "UTF-8");
					postData += "&userRepeatPassword=" + URLEncoder.encode(userRepeatPassword, "UTF-8");

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
								openErrorDialog(responseAPI.getAPImessage());
								userPasswordField.setText("");
								userRepeatPasswordField.setText("");
							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								openAccountCreatedDialog(responseAPI.getAPImessage());
							}
						});
					}
				} catch (ConnectException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							openErrorDialog("Failed to connect to the server");
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
