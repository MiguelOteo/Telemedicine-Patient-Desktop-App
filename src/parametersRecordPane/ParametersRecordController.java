package parametersRecordPane;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.bluetooth.RemoteDevice;


public class ParametersRecordController implements Initializable {

	private static final String MAC = "20:18:06:13:01:09";
	
	private boolean recordvalue = false;
	
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
		
		if (recordvalue == true){
			recordvalue = false;
			final String mac = MAC.replace(":", "");
			final int samplerate = 100;
		    final int[] analogs = {0};
			//start recording
			//bucle

		}
		else {
			//end recording
			recordvalue = true;
		}
		
		/*la idea es  usar este boton para empezar a grabar y parar
		 * dependiendo de una variable hará una cosa o la otra, cambiando a su vez el texto del boton
		 * 
		 * Se crea una lista

*/
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

