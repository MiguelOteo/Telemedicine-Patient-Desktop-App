package patient.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.ui.geometry.Side;
import de.gsi.dataset.spi.FloatDataSet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import patient.bitalino.BITalino;
import patient.bitalino.BITalinoException;
import patient.bitalino.Frame;
import patient.communication.AccountObjectCommunication;
import patient.models.APIRequest;
import patient.models.APIResponse;
import patient.models.BitalinoPackage;
import patient.treeobjects.PastBitalinoValuesTreeObject;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static patient.params.PatientParams.*;

public class ParametersRecordController implements Initializable {

	private boolean isECG = true;
	//private boolean isRecording = false;
	
	private final String MAC = AccountObjectCommunication.getMAC();

	private static Frame[] frame;
	
	@FXML
	private Pane mainPane;
	@FXML
	private StackPane viewPane;
	@FXML
	private Label nothingToShow;
	@FXML
	private JFXButton startRecording;
	@FXML
	private JFXButton changeGraph;
	@FXML
	private Label macLabel;
	@FXML
	private JFXTreeTableView<PastBitalinoValuesTreeObject> pastValuesTreeView;
	@FXML
	private final ObservableList<PastBitalinoValuesTreeObject> recordsObjects = FXCollections.observableArrayList();
	
	// Chart variables declarations
	private XYChart dataChart;

	private final FloatDataSet ECGdataSet = new FloatDataSet("ECG Records");

	private final FloatDataSet EMGdataSet = new FloatDataSet("EMG Records");

	private final DefaultNumericAxis xAxis = new DefaultNumericAxis("Time", "Hundredths of a second");

	private final DefaultNumericAxis yAxis = new DefaultNumericAxis("Records", "mV");

	private final float[] timeArray = new float[BLOCK_SIZE];

	private final float[] ECGdataArray = new float[BLOCK_SIZE];

	private final float[] EMGdataArray = new float[BLOCK_SIZE];

	public void initialize(URL location, ResourceBundle resources) {
		
		loadTreeTable();
		
		xAxis.setSide(Side.BOTTOM);
		yAxis.setSide(Side.LEFT);
		dataChart = new XYChart(xAxis, yAxis);
		final Zoomer zoom = new Zoomer();
		dataChart.setLegendVisible(false);
		zoom.omitAxisZoomList().add(yAxis);
		zoom.setSliderVisible(false);
		dataChart.setTitle("Last ECG package recorded");
		dataChart.getPlugins().add(zoom);
		dataChart.getDatasets().add(EMGdataSet);
		viewPane.getChildren().add(dataChart);
	
		macLabel.setText(MAC);
		pastValuesTreeView.setPlaceholder(new Label("No data available to show"));
		
		for (int j = 0; j < BLOCK_SIZE; j++) {
			timeArray[j] = j;
		}
	}

	@FXML
	private void startStopRecording() {

		if(!AccountObjectCommunication.isRecording()) {
			
			AccountObjectCommunication.setRecording(true);
			changeGraph.setDisable(true);
			
			int patientId = AccountObjectCommunication.getPatient().getPatientId();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			
			nothingToShow.setText("Recording has started, please wait");
			startRecording.setText("Stop Recording");
			
			doRecording(patientId, dateFormat);
			
		} else {
			
			AccountObjectCommunication.setRecording(false);
			startRecording.setText("Start Recording");
			nothingToShow.setText("Recording has stopped");
		}
	}

	private void doRecording(int patientId, DateFormat dateFormat) {
		
		Thread threadObject = new Thread("Recording BITalino data") {
			public void run() {
				
				BITalino bitalino = new BITalino();
				try {
					
					bitalino.open(MAC, SAMPLING_RATE);

					// Selection of channels 0 and 1 form BITalino
					int[] channelsToAcquire = { 0, 1 };
					bitalino.start(channelsToAcquire);

					while(AccountObjectCommunication.isRecording()) {
						
						frame = bitalino.read(BLOCK_SIZE);
						Timestamp now = new Timestamp(System.currentTimeMillis());

						StringBuilder emgValues = new StringBuilder("[");
						StringBuilder ecgValues = new StringBuilder("[");

						for (Frame value : frame) {
							emgValues.append(value.analog[0]).append(",");
							ecgValues.append(value.analog[1]).append(",");
						}

						emgValues = new StringBuilder(emgValues.substring(0, emgValues.length() - 1));
						ecgValues = new StringBuilder(ecgValues.substring(0, ecgValues.length() - 1));
						emgValues.append("]");
						ecgValues.append("]");

						BitalinoPackage bitalinoPack = new BitalinoPackage(patientId, SAMPLING_RATE, now, emgValues.toString(),
								ecgValues.toString());
						AccountObjectCommunication.getPatient().addNewPackage(bitalinoPack);

						// Sent the data to the RestAPI
						sendData(bitalinoPack);
						
						// Show the last package inserted in the chart
						updateChartData(bitalinoPack);
						
						// Reload the treeTableView with the new added package
						loadData();
					}
					bitalino.stop();
					
				} catch (Throwable ex) {
					Logger.getLogger(ParametersRecordController.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					try {
						bitalino.close();
					} catch (BITalinoException ex) {
						Logger.getLogger(ParametersRecordController.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

			}
		};
		threadObject.start();
	}
	 
	// Show the last package inserted in the chart
	private void updateChartData(BitalinoPackage bitalinoPack) {
		
		String graphECG = bitalinoPack.getEcgData();
		graphECG = graphECG.substring(1, graphECG.length() - 1);
		ArrayList<String> graphECGList = new ArrayList<>(Arrays.asList(graphECG.split(",")));

		String graphEMG = bitalinoPack.getEmgData();
		graphEMG = graphEMG.substring(1, graphEMG.length() - 1);
		ArrayList<String> graphEMGList = new ArrayList<>(Arrays.asList(graphEMG.split(",")));

		for (int n = 0; n < BLOCK_SIZE; n++) {
			timeArray[n] = n ;
			ECGdataArray[n] = Integer.parseInt(graphECGList.get(n));
			EMGdataArray[n] = Integer.parseInt(graphEMGList.get(n));
		}

		ECGdataSet.clearData();
		ECGdataSet.add(timeArray, ECGdataArray);

		EMGdataSet.clearData();
		EMGdataSet.add(timeArray, EMGdataArray);
	}

	// Sends each package of BITalino recordings to the APIRest
	private void sendData(BitalinoPackage bitalinoPack) {
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/addPacketsToPatient")
					.openConnection();

			connection.setRequestMethod("POST");

			Gson gsonConverter = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
			APIRequest requestAPI = new APIRequest();
			requestAPI.setBitalinoPackage(bitalinoPack);

			String postData = "APIRequest=" + URLEncoder.encode(gsonConverter.toJson(requestAPI), StandardCharsets.UTF_8);

			connection.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(postData);
			writer.flush();

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = inputReader.readLine()) != null) {
				response.append(inputLine);
			}
			inputReader.close();

			APIResponse responseAPI = gsonConverter.fromJson(response.toString(), APIResponse.class);

			if (!responseAPI.isError()) {
				Platform.runLater(() -> {
					startRecording.setDisable(false);
					changeGraph.setDisable(false);

				});
			} else {
				Platform.runLater(() -> {
					openDialog(responseAPI.getAPImessage());
					startRecording.setDisable(false);
					changeGraph.setDisable(false);

				});
			}

		} catch (ConnectException | FileNotFoundException connectionError) {
			Platform.runLater(() -> {
				openDialog("Failed to connect to the server");
				connectionError.printStackTrace();
				startRecording.setDisable(false);
				changeGraph.setDisable(false);
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	@FXML
	private void changeChart() {

		if (isECG) { // If true then ECG graph has to be change

			isECG = false;
			changeGraph.setText("Show ECG Recording");
			dataChart.setTitle("Last EMG package recorded");
			dataChart.getDatasets().clear();
			dataChart.getDatasets().add(EMGdataSet);

		} else {

			isECG = true;
			changeGraph.setText("Show EMG Recording");
			dataChart.setTitle("Last ECG package recorded");
			dataChart.getDatasets().clear();
			dataChart.getDatasets().add(ECGdataSet);
		}
	}

	private void loadData() {
		int count = 0;
		recordsObjects.clear();
		for (BitalinoPackage pack: AccountObjectCommunication.getPatient().getMeasuredPackages()) {
			recordsObjects.add(new PastBitalinoValuesTreeObject(count + 1, pack.getRecordsDate().toString(), pack.getRecordFreq()));
			count++;
		}
		pastValuesTreeView.refresh();
	}

	private void loadTreeTable() {

		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> packetId = new JFXTreeTableColumn<>("Packet ID");
		// bitalinoName.setPrefWidth(155);
		packetId.setCellValueFactory(
				param -> param.getValue().getValue().getTreePacketID());
		packetId.setResizable(false);

		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> packetDate = new JFXTreeTableColumn<>(
				"Date of Recording");
		packetDate.setPrefWidth(160);
		packetDate.setCellValueFactory(
				param -> param.getValue().getValue().getTreePacketDate());
		packetDate.setResizable(false);

		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> samplingRate = new JFXTreeTableColumn<>(
				"Sampling Rate");
		samplingRate.setPrefWidth(140);
		samplingRate.setCellValueFactory(
				param -> param.getValue().getValue().getTreeSamplingRate());
		samplingRate.setResizable(false);

		JFXTreeTableColumn<PastBitalinoValuesTreeObject, JFXButton> visualize = new JFXTreeTableColumn<>("Visualize");
		visualize.setPrefWidth(120);
		visualize.setCellValueFactory(
				param -> param.getValue().getValue().getViewRecord());
		visualize.setResizable(false);

		TreeItem<PastBitalinoValuesTreeObject> root = new RecursiveTreeItem<>(
				recordsObjects, RecursiveTreeObject::getChildren);
		pastValuesTreeView.setSelectionModel(null);
		pastValuesTreeView.getColumns().setAll(Arrays.asList(packetId, packetDate, samplingRate, visualize));
		pastValuesTreeView.setRoot(root);
		pastValuesTreeView.setShowRoot(false);
	}

	private void openDialog(String message) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(DIALOG_POP_UP_VIEW));
			Parent root = loader.load();
			DialogPopUpController controller = loader.getController();
			controller.setMessage(message);
			Stage stage = new Stage();
			stage.setHeight(130);
			stage.setWidth(300);
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Telelepsia Message");
			stage.getIcons().add(new Image(APP_ICON));
			
			// Set the pop-up in the center of the main menu window
			stage.setX(LogInController.getStage().getX() + LogInController.getStage().getWidth() / 2 - stage.getWidth() / 2);
			stage.setY(-75 + LogInController.getStage().getY() + LogInController.getStage().getHeight() / 2 - stage.getHeight() / 2);
			
			AccountObjectCommunication.getAnchorPane().setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> AccountObjectCommunication.getAnchorPane().setEffect(null));
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	@FXML
	private void minWindow() {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	private void closeApp() {
		System.exit(0);
	}
}
