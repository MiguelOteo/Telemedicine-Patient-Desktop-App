package patient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import launch.LaunchApp;
import patient.communication.AccountObjectCommunication;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static patient.params.PatientParams.*;

public class PatientMenuController implements Initializable {

	@FXML
	private AnchorPane menuWindow;
	@FXML
	private Pane menuMainPane;
	@FXML
	private JFXButton logOutButton;
	@FXML
	private JFXButton openBitalinoConnection;
	@FXML
	private JFXButton openPatientAccount;
	@FXML
	private JFXButton openBitalinoRecord;
	@FXML
	private JFXButton patientMessenger;

	public PatientMenuController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		AccountObjectCommunication.setAnchorPane(menuWindow);
		AccountObjectCommunication.setButtonControl1(openBitalinoRecord);
		
		if (AccountObjectCommunication.getPatient().getPatientIdNumber() == null) {
			Platform.runLater(this::openIdPopUp);
		} else {
			openPatientAccount();
		}
		
		openBitalinoRecord.setDisable(true);
	}

	@FXML
	private void logOut() {
		Stage stage = (Stage) logOutButton.getScene().getWindow();
		stage.close();
		AccountObjectCommunication.setMAC("");
		AccountObjectCommunication.setController(null);
		AccountObjectCommunication.setRecording(false);
		AccountObjectCommunication.setPatient(null);
		LaunchApp.getStage().show();
	}

	@FXML
	private void openPatientAccount() {
		
		Pane patientAccountPane;
		try {
			patientAccountPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(PATIENT_ACCOUNT_VIEW)));
			menuMainPane.getChildren().removeAll();
			menuMainPane.getChildren().setAll(patientAccountPane);
			openPatientAccount.setDisable(true);
			openBitalinoConnection.setDisable(false);
			patientMessenger.setDisable(false);
			if (!AccountObjectCommunication.getMAC().equals("")) {
				openBitalinoRecord.setDisable(false);
			}
		} catch (IOException error) {
			error.printStackTrace();
		}	
	}

	@FXML
	private void openBitalinoRecord() {
		Pane paramRecordPane;
		try {
		
			paramRecordPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(PARAMETERS_RECORD_VIEW)));
			menuMainPane.getChildren().removeAll();
			menuMainPane.getChildren().setAll(paramRecordPane);
			patientMessenger.setDisable(false);
			openPatientAccount.setDisable(false);
			openBitalinoConnection.setDisable(false);
			openBitalinoRecord.setDisable(true);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	@FXML
	private void openBitalinoConnectivity() {
		
		if (AccountObjectCommunication.getMAC().equals("")) {
			Pane bitalinoConnectivityPane;
			try {
				bitalinoConnectivityPane = FXMLLoader
						.load(Objects.requireNonNull(getClass().getResource(BITALINO_CONNECTION_VIEW)));
				menuMainPane.getChildren().removeAll();
				menuMainPane.getChildren().setAll(bitalinoConnectivityPane);
				openPatientAccount.setDisable(false);
				openBitalinoConnection.setDisable(true);	
				patientMessenger.setDisable(false);
				openBitalinoRecord.setDisable(true);
			} catch (IOException error) {
				error.printStackTrace();
			}
		} else {
			Pane bitalinoConnectedPane;
			try {
				bitalinoConnectedPane = FXMLLoader
						.load(Objects.requireNonNull(getClass().getResource(BITALINO_CONNECTED_VIEW)));
				menuMainPane.getChildren().removeAll();
				menuMainPane.getChildren().setAll(bitalinoConnectedPane);
				openPatientAccount.setDisable(false);
				openBitalinoConnection.setDisable(true);
				patientMessenger.setDisable(false);
				openBitalinoRecord.setDisable(false);
			} catch (IOException error) {
				error.printStackTrace();
			}
		}
	}
	
	@FXML
	private void openPatientMessenger() {
		Pane patientMessengerPane;
		try {
			patientMessengerPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(PATIENT_MESSENGER_VIEW)));
			menuMainPane.getChildren().removeAll();
			menuMainPane.getChildren().setAll(patientMessengerPane);
			openPatientAccount.setDisable(false);
			openBitalinoConnection.setDisable(false);
			patientMessenger.setDisable(true);
			if (!AccountObjectCommunication.getMAC().equals("")) {
				openBitalinoRecord.setDisable(false);
			}
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	private void openIdPopUp() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(INSERT_ID_VIEW));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setHeight(160);
			stage.setWidth(310);
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Insert ID Number");
			stage.getIcons().add(new Image(APP_ICON));
			
			// Set the pop-up in the center of the main menu window
			stage.setX(LogInController.getStage().getX() + LogInController.getStage().getWidth() / 2 - stage.getWidth() / 2);
			stage.setY(-75 + LogInController.getStage().getY() + LogInController.getStage().getHeight() / 2 - stage.getHeight() / 2);
			
			menuWindow.setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> {
				menuWindow.setEffect(null);
				openPatientAccount();
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
}
