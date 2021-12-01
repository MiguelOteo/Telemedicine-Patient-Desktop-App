package patientRecordsPane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.gillius.jfxutils.chart.ChartZoomManager;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;

import commonParams.CommonParams;
import communication.AccountObjectCommunication;
import dialogPopUp.DialogPopUpController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.APIRequest;
import models.APIResponse;
import models.BitalinoPackage;
import models.Patient;

public class PatientRecordsController implements Initializable {

	private Patient patient = new Patient();
	private boolean isECG = true;
	
	@FXML
	private Pane mainPane;
	@FXML
	private JFXButton changeGraph;
	@FXML
	private StackPane chartPane;
	@FXML
	private Rectangle selectRect;
	@FXML
	private Label patientName;
	@FXML
	private Label patientEmail;
	@FXML
	private Label patientIdNumber;
	@FXML
	private DatePicker datePicker;
	@FXML
	private LineChart<Number, Number> measuresChart;
 
	private LocalTime[] dayTimeVector;
	
	private final XYChart.Series<Number, Number> dataSeries = new XYChart.Series<Number, Number>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		measuresChart.setTitle("ECG Recordings of the selected day");
		changeGraph.setText("Show EMG Recording");
		
		this.patient.setPatientId(AccountObjectCommunication.getDatabaseId());

		getPatientInformation();
		
		createDayArray(1000);
		
		ChartZoomManager zoomManager = new ChartZoomManager(chartPane, selectRect, measuresChart);
		zoomManager.start();
		
		datePicker.valueProperty().addListener((observable, oldDate, newDate)->{
			getPatientDayData(Date.valueOf(newDate));
		});	
	}
	
	@FXML
	private void changeChart(MouseEvent event) {
		
		dataSeries.getData().clear();
		
		if(isECG) { // If true then ECG graph has to be change
			measuresChart.setTitle("EMG Recordings of the selected day");
			changeGraph.setText("Show ECG Recording");
			isECG = false;
			dataSeries.getData().add(new XYChart.Data<Number, Number>(1, 24));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(2, 22));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(3, 45));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(4, 23));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(5, 34));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(6, 36));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(7, 14));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(8, 15));	
			
		} else {
			measuresChart.setTitle("ECG Recordings of the selected day");
			changeGraph.setText("Show EMG Recording");
			isECG = true;
			dataSeries.getData().add(new XYChart.Data<Number, Number>(1, 23));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(2, 14));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(3, 15));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(4, 24));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(5, 34));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(6, 36));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(7, 22));
			dataSeries.getData().add(new XYChart.Data<Number, Number>(8, 45));
		}
		measuresChart.getData().clear();
		measuresChart.getData().add(dataSeries);
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
	
	private void loadInformation() {
		patientName.setText("Name: " + patient.getName());
		if(patient.getPatientIdNumber() != null) {
			patientIdNumber.setText("Patient ID: " + patient.getPatientIdNumber());
		} else {
			patientIdNumber.setText("Patient ID: not inserted");
		}
		patientEmail.setText("Patient email: " + patient.getEmail());
	}
	
	// Displays any error returned form the Rest API
	private void openDialog(String message) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialogPopUp/dialogPopUpLayout.fxml"));
			Parent root = (Parent) loader.load();
			DialogPopUpController controler = loader.getController();
			controler.setMessage(message);
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			AccountObjectCommunication.getAnchorPane().setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> {
					AccountObjectCommunication.getAnchorPane().setEffect(null);
			});
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	private void turnStringToIntArray(BitalinoPackage bitalinoPackage) {

		String data = "";//bitalinoPackage.getRecordsData();
		
		int[] dataArray = Arrays.stream(data.substring(1, data.length()-1).split(","))
			    .map(String::trim).mapToInt(Integer::parseInt).toArray();
		
		Timestamp packageStartingDate = bitalinoPackage.getRecordsDate();
	}
	 
	private void createDayArray(int frequency) {
		
		Thread threadObject = new Thread("CreatingDayArray") {
			public void run() {
				
				// Samples in a day
				int samples = frequency * 24 * 60 * 60;
				
				dayTimeVector = new LocalTime[samples];
				
				LocalTime time = LocalTime.MIN;
				int gap = (1/frequency) * 1000;
				
				for(int n = 0; n < samples; n++) {
					dayTimeVector[n] = time.plus(gap, ChronoUnit.MILLIS);
				}
			}
		};
		threadObject.start();
	}
	
	private void getPatientDayData(Date selectedDate) {
		
		Thread threadObject = new Thread("GettingDayData") {
			public void run() {
				
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/GetPatientDayRecords")
							.openConnection();
					connection.setRequestMethod("POST");
					
					APIRequest requestAPI = new APIRequest();
					requestAPI.setPatientId(patient.getPatientId());
					requestAPI.setDate(selectedDate);
					String postData = "APIRequest=" + URLEncoder.encode(new Gson().toJson(requestAPI), "UTF-8");
					
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(postData);
					writer.flush();

					BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = inputReader.readLine()) != null) {
						response.append(inputLine);
					}
					inputReader.close();
					
					APIResponse responseAPI = new Gson().fromJson(response.toString(), APIResponse.class);
					
					if (!responseAPI.isError()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								patient.getMeasuredPackages().clear();
								patient.setMeasuredPackages(responseAPI.getDayRecords());
								
								// TODO - Insert data into the arrays
								//for(BitalinoPackage pack: patient.getMeasuredPackages()) {
									//int[] data = turnStringToIntArray(pack.getRecordsData());
									//System.out.println(data.toString());
								//}
							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								openDialog(responseAPI.getAPImessage());
							}
						});
					}
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							conncetionError.printStackTrace();
							openDialog("Failed to connect to the server");
						}
					});
				} catch (IOException error) {
					error.printStackTrace();
				}		
			}
		};
		threadObject.start();
	}
	
	private void getPatientInformation() {
		Thread threadObject = new Thread("gettingPatientInfo") {
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/getPatientInformation")
							.openConnection();

					connection.setRequestMethod("POST");
					
					APIRequest requestAPI = new APIRequest();
					requestAPI.setPatientId(patient.getPatientId());
					String postData = "APIRequest=" + URLEncoder.encode(new Gson().toJson(requestAPI), "UTF-8");
					
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(postData);
					writer.flush();

					BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = inputReader.readLine()) != null) {
						response.append(inputLine);
					}
					inputReader.close();

					APIResponse responseAPI = new Gson().fromJson(response.toString(), APIResponse.class);
					
					if (!responseAPI.isError()) {
						patient = responseAPI.getPatient();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								loadInformation();
							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								openDialog(responseAPI.getAPImessage());
							}
						});
					}
				} catch (ConnectException | FileNotFoundException conncetionError) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							conncetionError.printStackTrace();
							openDialog("Failed to connect to the server");
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
