package patient.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import patient.communication.AccountObjectCommunication;

public class BitalinoConnectedController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private JFXButton closeConnection;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	@FXML
	private void closeConnection() {
		AccountObjectCommunication.setMAC("");
		AccountObjectCommunication.getButtonControl1().setDisable(true);
		Pane bitalinoConnectivityPane;
		try {
			bitalinoConnectivityPane = FXMLLoader.load(getClass().getResource("/patient/view/BitalinoConnectionLayout.fxml"));
			mainPane.getChildren().removeAll();
			mainPane.getChildren().setAll(bitalinoConnectivityPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	@FXML
	private void minWindow(MouseEvent event) {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
	}
	
	@FXML
	private void closeApp(MouseEvent event) {
		System.exit(0);
	}
}
