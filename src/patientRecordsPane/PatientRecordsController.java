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
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;

import commonParams.CommonParams;
import communication.AccountObjectCommunication;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.ui.geometry.Side;
import de.gsi.dataset.spi.DoubleDataSet;
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
	
	private final DoubleDataSet ECGdataSet = new DoubleDataSet("ECG Records");
	
    private final DoubleDataSet EMGdataSet = new DoubleDataSet("EMG Records");
    
    private DefaultNumericAxis xAxis = new DefaultNumericAxis("Time", "Seconds");
    
    private DefaultNumericAxis yAxis = new DefaultNumericAxis("Records", "mV");
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		changeGraph.setText("Show EMG Recording");
		this.patient.setPatientId(AccountObjectCommunication.getDatabaseId());
		getPatientInformation();

		datePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
			getPatientDayData(Date.valueOf(newDate));
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
					final int N_SAMPLES = 60000;
					
					final double[] xValues = new double[N_SAMPLES];
				    final double[] yValues2 = new double[N_SAMPLES];
				    for (int n = 0; n < N_SAMPLES; n++) {
				    	xValues[n] = n;
				        yValues2[n] = Math.sin(Math.toRadians(10.0 * n));
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
					final int N_SAMPLES = 60000;
					
					final double[] xValues = new double[N_SAMPLES];
				    final double[] yValues1 = new double[N_SAMPLES];
				    for (int n = 0; n < N_SAMPLES; n++) {
				    	xValues[n] = n;
				        yValues1[n] = Math.cos(Math.toRadians(10.0 * n));
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

	/*private void addBitalinoDataToGraphArrays(BitalinoPackage bitalinoPackage) {

		String ECGdata = bitalinoPackage.getecgData();
		String EMGdata = bitalinoPackage.getemgData();
		
		Thread threadObject = new Thread("AddingData") {
			public void run() {
				
				int[] ECGdataArray = Arrays.stream(ECGdata.substring(1, ECGdata.length() - 1).split(","))
						.map(String::trim).mapToInt(Integer::parseInt).toArray();

				int[] EMGdataArray = Arrays.stream(EMGdata.substring(1, EMGdata.length() - 1).split(","))
						.map(String::trim).mapToInt(Integer::parseInt).toArray();

				LocalTime startingDate = bitalinoPackage.getRecordsDate().toLocalDateTime().toLocalTime();

				int timePos = 0;

				for (timePos = 0; timePos < dayTimeVector.length; timePos++) {
					if (dayTimeVector[timePos].equals(startingDate)) {
						break;
					}
				}

				for (int n = timePos; n < timePos + ECGdataArray.length; n++) {
					dayECGDadaVector[n] = ECGdataArray[n - timePos];
					dayEMGDadaVector[n] = EMGdataArray[n - timePos];
				}
			}
		};
		threadObject.start();
	}*/

	/*@SuppressWarnings("unused")
	private void createDayAndDataArray(int frequency) {

		Thread threadObject = new Thread("CreatingDayArray") {
			public void run() {

				// Samples in a half an hour
				int samples = frequency * 30 * 60;

				dayTimeVector = new LocalTime[samples];
				dayECGDadaVector = new int[samples];
				dayEMGDadaVector = new int[samples];

				LocalTime time = LocalTime.MIN;

				dayTimeVector[0] = time;
				dayECGDadaVector[0] = 0;
				dayEMGDadaVector[0] = 0;
				for (int n = 1; n < samples; n++) {
					time = time.plus(1, ChronoUnit.MILLIS);
					dayTimeVector[n] = time;
					dayECGDadaVector[n] = 0;
					dayEMGDadaVector[n] = 0;
				}
			}
		};
		threadObject.start();
	}*/

	private void getPatientDayData(Date selectedDate) {

		Thread threadObject = new Thread("GettingDayData") {
			public void run() {

				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(
							CommonParams.BASE_URL + "/GetPatientDayRecords").openConnection();
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

								//for (BitalinoPackage pack : patient.getMeasuredPackages()) {
									//addBitalinoDataToGraphArrays(pack);
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
