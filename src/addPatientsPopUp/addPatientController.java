package addPatientsPopUp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class addPatientController implements Initializable {

	@FXML
	private JFXButton cancelOperation;
	@FXML
	private JFXButton addSelectedPatients;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	private void cancelOperation(MouseEvent event) throws IOException {
		Stage stage = (Stage) cancelOperation.getScene().getWindow();
		stage.close();
	}

}
