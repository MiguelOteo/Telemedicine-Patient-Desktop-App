package patient.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import patient.communication.AccountObjectCommunication;

import static patient.params.PatientParams.*;

public class BitalinoConnectedController implements Initializable {

	@FXML
	private Pane mainPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	@FXML
	private void closeConnection() {
		AccountObjectCommunication.setMAC("");
		AccountObjectCommunication.getButtonControl1().setDisable(true);
		Pane bitalinoConnectivityPane;
		try {
			bitalinoConnectivityPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(BITALINO_CONNECTION_VIEW)));
			mainPane.getChildren().removeAll();
			mainPane.getChildren().setAll(bitalinoConnectivityPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	@FXML
	private void minWindow() {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
	}
	
	@FXML
	private void closeApp() {
		System.exit(0);
	}
}
