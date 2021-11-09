package insertIdPopUp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import communication.AccountObjectCommunication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import remoteParams.RestAPI;


public class insertIdController implements Initializable {

	@FXML
	JFXButton confirmButton;
	@FXML
	JFXTextField idField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		confirmButton.setOnAction((ActionEvent event) -> {
			
			if(idField.getText() != null) {
				confirmButton.setDisable(true);
				//updateId(idField.getText());
			}
		});
	}
	
	@FXML
	private void closeDialog(MouseEvent event) throws IOException {
		Stage stage = (Stage) confirmButton.getScene().getWindow();
		stage.close();
	}
	
	@SuppressWarnings("unused")
	private void updateId(String idValue) {
		Thread threadObject = new Thread("UpdatingUserId") {
			public void run() {
				
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(RestAPI.BASE_URL + "/updateId").openConnection();

					connection.setRequestMethod("POST");

					String postData = "";
					
					if(AccountObjectCommunication.getDoctor() != null) {
			
						postData = "doctorId=" + URLEncoder.encode(Integer.toString(AccountObjectCommunication.getDoctor().getDoctorId()), "UTF-8");
						postData += "&doctorIdentification=" + URLEncoder.encode(idValue, "UTF-8");
					} else {
						
						postData = "patientId=" + URLEncoder.encode(Integer.toString(AccountObjectCommunication.getPatient().getPatientId()), "UTF-8");
						postData += "&patientIdNumber=" + URLEncoder.encode(idValue, "UTF-8");
					}
					
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(postData);
					writer.flush();
					
					
						
				} catch (ConnectException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							
						}
					});
				} catch (IOException error) {
					error.printStackTrace();
				}
			}
		};
		threadObject.start();
	}
	

}
