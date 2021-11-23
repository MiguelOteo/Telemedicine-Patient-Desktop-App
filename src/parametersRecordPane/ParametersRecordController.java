package parametersRecordPane;


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

public class ParametersRecordController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private Pane viewPane;
	@FXML
	private Pane pastPane;
	@FXML
	private JFXButton startRecording;
	@FXML
	private JFXButton comparePast;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	@FXML
	private void startRecording() {
		

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

