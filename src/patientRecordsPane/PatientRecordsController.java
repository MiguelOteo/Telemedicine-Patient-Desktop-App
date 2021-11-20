package patientRecordsPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PatientRecordsController implements Initializable {

	@FXML
	private Pane mainPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
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
	
	@FXML
	private void goBack(MouseEvent event) {
		Pane doctorPatientsPane;
		try {
			doctorPatientsPane = FXMLLoader.load(getClass().getResource("/doctorPatientsPane/DoctorPatientsLayout.fxml"));
			mainPane.getChildren().removeAll();
			mainPane.getChildren().setAll(doctorPatientsPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
}
