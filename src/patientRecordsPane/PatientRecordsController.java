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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import commonParams.CommonParams;
import communication.AccountObjectCommunication;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.ui.geometry.Side;
import de.gsi.dataset.spi.FloatDataSet;
import de.gsi.chart.XYChart;
import dialogPopUp.DialogPopUpController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
	private StackPane chartPane;
	@FXML
	private JFXButton changeGraph;
	@FXML
	private JFXComboBox<String> timeSelection;
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
	
	private XYChart dataChart;
	
	private final FloatDataSet ECGdataSet = new FloatDataSet("ECG Records");
	
    private final FloatDataSet EMGdataSet = new FloatDataSet("EMG Records");
    
    private DefaultNumericAxis xAxis = new DefaultNumericAxis("Time", "Milliseconds");
    
    private DefaultNumericAxis yAxis = new DefaultNumericAxis("Records", "mV");
    
    // Array to store the 20 minutes gap in milliseconds
    private int[] time20Array;
    
    // Arrays to store all the recording data form the BITalino packages request
    private float[] ECGdataArray;
    
    private float[] EMGdataArray;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Creates the time values for the time combo box 
		for(int hour = 0; hour < 10; hour++) {
			timeSelection.getItems().addAll(" 0" + hour + ":00", " 0" + hour + ":20", " 0" + hour + ":40");
		}
		for(int hour = 10; hour < 24; hour++) {
			timeSelection.getItems().addAll(" " + hour + ":00", " " + hour + ":20", " " + hour + ":40");
		}
		timeSelection.setDisable(true);
		
		changeGraph.setText("Show EMG Recording");
		this.patient.setPatientId(AccountObjectCommunication.getDatabaseId());
		getPatientInformation();
		createSamplesArrays();

		// When a new date is selected then unable the time combo box
		datePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
			timeSelection.setDisable(false);
		});
		
		// Every time a new time is selected then a request is sent
		timeSelection.valueProperty().addListener((observable, oldTime, newTime) -> {
			getPatientDayData(Timestamp.valueOf(datePicker.getValue() + newTime + ":00.000"));
		});
		
		xAxis.setSide(Side.BOTTOM);
		yAxis.setSide(Side.LEFT);
		
		dataChart = new XYChart(xAxis, yAxis);
		final Zoomer zoom = new Zoomer();
		zoom.omitAxisZoomList().add(yAxis);
		zoom.setSliderVisible(false);
		dataChart.getPlugins().add(zoom);
		chartPane.getChildren().add(dataChart);
	}

	@FXML
	private void changeChart(MouseEvent event) {
		
		Thread threadObject = new Thread("Creating data set and showing it") {
			public void run() {
				
				if (isECG) { // If true then ECG graph has to be change
					isECG = false;
					final int N_SAMPLES = 120000;
					
					final float[] xValues = new float[N_SAMPLES];
				    final float[] yValues2 = new float[N_SAMPLES];
				    for (int n = 0; n < N_SAMPLES; n++) {
				    	xValues[n] = n;
				        yValues2[n] = (float) Math.sin(Math.toRadians(10.0 * n));
				    }
				    EMGdataSet.set(xValues, yValues2);

				    Platform.runLater(new Runnable() {
						@Override
						public void run() {
							changeGraph.setText("Show ECG Recording");
						    dataChart.getDatasets().clear();
						    dataChart.getDatasets().add(EMGdataSet);
						}
					}); 
				} else {
					isECG = true;	
					final int N_SAMPLES = 120000;
					
					final float[] xValues = new float[N_SAMPLES];
				    final float[] yValues1 = new float[N_SAMPLES];
				    for (int n = 0; n < N_SAMPLES; n++) {
				    	xValues[n] = n;
				    	 yValues1[n] = (float) Math.sin(Math.toRadians(10.0 * n));
				    }
				    ECGdataSet.set(xValues, yValues1);
				    
				    Platform.runLater(new Runnable() {
						@Override
						public void run() {
							changeGraph.setText("Show EMG Recording");
						    dataChart.getDatasets().clear();
						    dataChart.getDatasets().add(ECGdataSet);
						}
					});
				}
			}
		};
		threadObject.start();
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
			doctorPatientsPane = FXMLLoader
					.load(getClass().getResource("/doctorPatientsPane/DoctorPatientsLayout.fxml"));
			mainPane.getChildren().removeAll();
			mainPane.getChildren().setAll(doctorPatientsPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	private void loadInformation() {
		patientName.setText("Name: " + patient.getName());
		if (patient.getPatientIdNumber() != null) {
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

	private void addBitalinoDataToGraphArrays(BitalinoPackage bitalinoPackage) {

		//String ECGdata = bitalinoPackage.getecgData();
		//String EMGdata = bitalinoPackage.getemgData();
		
		Thread threadObject = new Thread("AddingData") {
			public void run() {
				
				/*int[] ECGdataPackage = Arrays.stream(ECGdata.substring(1, ECGdata.length() - 1).split(","))
						.map(String::trim).mapToInt(Integer::parseInt).toArray();

				int[] EMGdataPackage = Arrays.stream(EMGdata.substring(1, EMGdata.length() - 1).split(","))
						.map(String::trim).mapToInt(Integer::parseInt).toArray();*/
				
				Calendar calendar = Calendar.getInstance();
				
				Timestamp time = Timestamp.valueOf("2021-09-08 07:45:35.82");
				
				calendar.setTime(time);
				
				// Get the minutes in the 20 minutes segments (45 minutes -> 5 minutes form 40 to 60) 
				int minutes = calendar.get(Calendar.MINUTE)%20;
				
				int seconds = calendar.get(Calendar.SECOND);
				
				int hundredth = ((minutes * 60) + seconds) * 100;
				
				int timePos = 0;
				
				System.out.println("Centesima: " + hundredth);

				for (timePos = 0; timePos < time20Array.length; timePos++) {
					if (time20Array[timePos] == hundredth) {
						System.out.println("Centesima: " + hundredth + " y Posición: " + timePos);	
						break;
					}
				}

				for (int n = timePos; n < timePos + ECGdataArray.length; n++) {
					
				}
			}
		};
		threadObject.start();
	}

	private void createSamplesArrays() {

		Thread threadObject = new Thread("CreatingArrays") {
			public void run() {

				// Samples in a 20 minutes
				int samples = 100 * 30 * 20;

				time20Array = new int[samples];
				ECGdataArray = new float[samples];
				EMGdataArray = new float[samples];

				for (int n = 0; n < samples; n++) {
					time20Array[n] = n;
					ECGdataArray[n] = 0;
					EMGdataArray[n] = 0;
				}
			}
		};
		threadObject.start();
	}

	private void getPatientDayData(Timestamp selectedDate) {

		Thread threadObject = new Thread("GettingDayData") {
			public void run() {

				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(
							CommonParams.BASE_URL + "/getPatientDayRecords").openConnection();
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

								for (BitalinoPackage pack : patient.getMeasuredPackages()) {
									addBitalinoDataToGraphArrays(pack);
								}
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
					HttpURLConnection connection = (HttpURLConnection) new URL(
							CommonParams.BASE_URL + "/getPatientInformation").openConnection();

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
