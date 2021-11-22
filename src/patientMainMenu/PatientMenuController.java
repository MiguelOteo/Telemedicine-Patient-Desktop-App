package patientMainMenu;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import communication.AccountObjectCommunication;
import javafx.application.Platform;
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
	
	public PatientMenuController() {}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		AccountObjectCommunication.setAnchorPane(menuWindow);
		
		if(AccountObjectCommunication.getPatient().getPatientIdNumber() == null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					opendIdPopUp();
				}	
			});
		} else {
			openPatientAccout();
		}
	}
	
	@FXML
	private void closeApp(MouseEvent event) {
		System.exit(0);
	}

	@FXML
	private void logOut(MouseEvent event) {
		Stage stage = (Stage) logOutButton.getScene().getWindow();
		stage.close();
		AccountObjectCommunication.setPatient(null);
		LaunchApp.getStage().show();
	}
	
	@FXML
	private void minWindow(MouseEvent event) {
		Stage stage = (Stage) menuMainPane.getScene().getWindow();
		stage.setIconified(true);
	}
	
	@FXML
	private void openPatientAccout() {
		// TODO - Load a patient account pane
		openPatientAccount.setDisable(true);
		openBitalinoConnection.setDisable(false);
		openBitalinoRecord.setDisable(false);
	}
	
	@FXML
	private void openBitalinoRecord() {
		// TODO - Load a patient BITalino record pane
		openPatientAccount.setDisable(false);
		openBitalinoConnection.setDisable(false);
		openBitalinoRecord.setDisable(true);
	}
	
	@FXML
	private void openBitalinoConnectivity() {
		Pane bitalinoConnectivityPane;
		try {
			bitalinoConnectivityPane = FXMLLoader.load(getClass().getResource("/bitalinoConnectionPane/BitalinoConnectionLayout.fxml"));
			menuMainPane.getChildren().removeAll();
			menuMainPane.getChildren().setAll(bitalinoConnectivityPane);
			openPatientAccount.setDisable(false);
			openBitalinoConnection.setDisable(true);
			openBitalinoRecord.setDisable(false);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	private void opendIdPopUp() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/insertIdPopUp/InsertIdLayout.fxml"));
			Parent root = (Parent) loader.load();
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			menuWindow.setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> {
				menuWindow.setEffect(null);
				openPatientAccout();
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
}

