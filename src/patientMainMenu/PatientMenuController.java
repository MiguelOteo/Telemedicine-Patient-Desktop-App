package patientMainMenu;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import communication.AccountObjectCommunication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import launchApp.LaunchApp;

public class PatientMenuController implements Initializable {
	
	@FXML
	private AnchorPane menu_window;
	@FXML
	private Pane main_pane;
	@FXML
	private Pane menu_main_pane;
	@FXML
	private JFXButton logOut_button;
	@FXML
	private Label userNameLabel;
	@FXML
	private Label userEmailLabel;
	@FXML
	private Label userPatientIdLabel;
	@FXML
	private static Stage stage_window;
	

	public PatientMenuController() {}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userNameLabel.setText(AccountObjectCommunication.getPatient().getName());
		userEmailLabel.setText(AccountObjectCommunication.getPatient().getEmail());
	}
	

	@FXML
	private void close_app(MouseEvent event) {
		System.exit(0);
	}

	@FXML
	private void log_out(MouseEvent event) {
		Stage stage = (Stage) logOut_button.getScene().getWindow();
		stage.close();
		LaunchApp.getStage().show();
	}
	
	@FXML
	private void min_window(MouseEvent event) {
		Stage stage = (Stage) menu_main_pane.getScene().getWindow();
		stage.setIconified(true);
	}
}

