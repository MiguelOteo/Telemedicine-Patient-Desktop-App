package doctorAccountPane;

import java.net.URL;
import java.util.ResourceBundle;

import communication.AccountObjectCommunication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DoctorAccountController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private Label userNameLabel;
	@FXML
	private Label userEmailLabel;
	@FXML
	private Label userDoctorIdLabel;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		userNameLabel.setText("Name: " + AccountObjectCommunication.getDoctor().getName());
		userEmailLabel.setText("Doctor Email: " + AccountObjectCommunication.getDoctor().getEmail());
		userDoctorIdLabel.setText("Doctor Identification: " + AccountObjectCommunication.getDoctor().getDoctorIdNumber());
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
}
