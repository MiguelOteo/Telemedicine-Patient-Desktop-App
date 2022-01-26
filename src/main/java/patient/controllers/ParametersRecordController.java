package patient.controllers;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import patient.bitalino.BITalino;
import patient.bitalino.BITalinoException;
import patient.bitalino.Frame;
import patient.communication.AccountObjectCommunication;
import patient.models.APIRequest;
import patient.models.APIResponse;
import patient.models.BitalinoPackage;
import patient.params.PatientParams;
import patient.treeobjects.PastBitalinoValuesTreeObject;

public class ParametersRecordController implements Initializable {
	
	private boolean isECG = true;
	//private boolean isRecording = false;
	
	private String MAC = AccountObjectCommunication.getMAC();

	private static Frame[] frame;
	
	@FXML
	private Pane mainPane;
	@FXML
	private StackPane viewPane;
	@FXML
	private Label nothingtoshow;
	@FXML
	private JFXButton startRecording;
	@FXML
	private JFXButton changegraph;
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

	private DefaultNumericAxis xAxis = new DefaultNumericAxis("Time", "Hundredths of a second");

	private DefaultNumericAxis yAxis = new DefaultNumericAxis("Records", "mV");

	private final float[] timeArray = new float[PatientParams.BLOCK_SIZE];

	private final float[] ECGdataArray = new float[PatientParams.BLOCK_SIZE];

	private final float[] EMGdataArray = new float[PatientParams.BLOCK_SIZE];

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
		
		for (int j = 0; j < PatientParams.BLOCK_SIZE; j++) {
			timeArray[j] = j;
		}
	}

	@FXML
	private void startStopRecording(MouseEvent event) {

		if(!AccountObjectCommunication.isRecording()) {
			
			AccountObjectCommunication.setRecording(true);
			changegraph.setDisable(true);
			
			int patientId = AccountObjectCommunication.getPatient().getPatientId();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			
			nothingtoshow.setText("Recording has started, please wait");
			startRecording.setText("Stop Recording");
			
			doRecording(patientId, dateFormat);
			
		} else {
			
			AccountObjectCommunication.setRecording(false);
			startRecording.setText("Start Recording");
			nothingtoshow.setText("Recording has stopped");
		}
	}

	private void doRecording(int patientId, DateFormat dateFormat) {
		
		Thread threadObject = new Thread("Recording BITalino data") {
			public void run() {
				
				BITalino bitalino = new BITalino();
				try {
					
					bitalino.open(MAC, PatientParams.SAMPLING_RATE);

					// Selection of channels 0 and 1 form BITalino
					int[] channelsToAcquire = { 0, 1 };
					bitalino.start(channelsToAcquire);

					while(AccountObjectCommunication.isRecording()) {
						
						frame = bitalino.read(PatientParams.BLOCK_SIZE);
						Timestamp now = new Timestamp(System.currentTimeMillis());

						String emgValues = "[";
						String ecgValues = "[";
						for (int i = 0; i < frame.length; i++) {

							emgValues = emgValues + frame[i].analog[0] + ",";
							ecgValues = ecgValues + frame[i].analog[1] + ",";
						}
						emgValues = emgValues.substring(0, emgValues.length() - 1);
						ecgValues = ecgValues.substring(0, ecgValues.length() - 1);
						emgValues = emgValues + "]";
						ecgValues = ecgValues + "]";

						BitalinoPackage bitalinoPack = new BitalinoPackage(patientId, PatientParams.SAMPLING_RATE, now, emgValues,
								ecgValues);
						AccountObjectCommunication.getPatient().addNewPackage(bitalinoPack);

						// Sent the data to the RestAPI
						sendData(bitalinoPack);
						
						// Show the last package inserted in the chart
						updateChartData(bitalinoPack);
						
						// Reload the treeTableView with the new added package
						loadData();
					}
					bitalino.stop();
					
				} catch (BITalinoException ex) {
					Logger.getLogger(ParametersRecordController.class.getName()).log(Level.SEVERE, null, ex);
				} catch (Throwable ex) {
					Logger.getLogger(ParametersRecordController.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					try {
						if (bitalino != null) {
							bitalino.close();
						}
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
		
		String graphECG = bitalinoPack.getecgData();
		graphECG = graphECG.substring(1, graphECG.length() - 1);
		ArrayList<String> graphECGList = new ArrayList<>(Arrays.asList(graphECG.split(",")));

		String graphEMG = bitalinoPack.getemgData();
		graphEMG = graphEMG.substring(1, graphEMG.length() - 1);
		ArrayList<String> graphEMGList = new ArrayList<>(Arrays.asList(graphEMG.split(",")));

		for (int n = 0; n < PatientParams.BLOCK_SIZE; n++) {
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
			HttpURLConnection connection = (HttpURLConnection) new URL(PatientParams.BASE_URL + "/addPacketsToPatient")
					.openConnection();

			connection.setRequestMethod("POST");

			Gson gsonConverter = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
			APIRequest requestAPI = new APIRequest();
			requestAPI.setBitalinopackage(bitalinoPack);

			String postData = "APIRequest=" + URLEncoder.encode(gsonConverter.toJson(requestAPI), "UTF-8");

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

			APIResponse responseAPI = gsonConverter.fromJson(response.toString(), APIResponse.class);

			if (!responseAPI.isError()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						startRecording.setDisable(false);
						changegraph.setDisable(false);

					}
				});
			} else {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						openDialog(responseAPI.getAPImessage());
						startRecording.setDisable(false);
						changegraph.setDisable(false);

					}
				});
			}

		} catch (ConnectException | FileNotFoundException conncetionError) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					openDialog("Failed to connect to the server");
					conncetionError.printStackTrace();
					startRecording.setDisable(false);
					changegraph.setDisable(false);
				}
			});
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	@FXML
	private void changeChart(MouseEvent event) {

		if (isECG) { // If true then ECG graph has to be change

			isECG = false;
			changegraph.setText("Show ECG Recording");
			dataChart.setTitle("Last EMG package recorded");
			dataChart.getDatasets().clear();
			dataChart.getDatasets().add(EMGdataSet);

		} else {

			isECG = true;
			changegraph.setText("Show EMG Recording");
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
				new Callback<JFXTreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<PastBitalinoValuesTreeObject, String> param) {
						return param.getValue().getValue().getTreePacketid();
					}
				});
		packetId.setResizable(false);

		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> packetDate = new JFXTreeTableColumn<>(
				"Date of Recording");
		packetDate.setPrefWidth(160);
		packetDate.setCellValueFactory(
				new Callback<JFXTreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<PastBitalinoValuesTreeObject, String> param) {
						return param.getValue().getValue().getTreePacketdate();
					}
				});
		packetDate.setResizable(false);

		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> samplingRate = new JFXTreeTableColumn<>(
				"Sampling Rate");
		samplingRate.setPrefWidth(140);
		samplingRate.setCellValueFactory(
				new Callback<JFXTreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<PastBitalinoValuesTreeObject, String> param) {
						return param.getValue().getValue().getTreeSamplingRate();
					}
				});
		samplingRate.setResizable(false);

		JFXTreeTableColumn<PastBitalinoValuesTreeObject, JFXButton> visualize = new JFXTreeTableColumn<>("Visualize");
		visualize.setPrefWidth(120);
		visualize.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject, JFXButton>, ObservableValue<JFXButton>>() {
					@Override
					public ObservableValue<JFXButton> call(
							CellDataFeatures<PastBitalinoValuesTreeObject, JFXButton> param) {
						return param.getValue().getValue().getViewRecord();
					}
				});
		visualize.setResizable(false);

		TreeItem<PastBitalinoValuesTreeObject> root = new RecursiveTreeItem<PastBitalinoValuesTreeObject>(
				recordsObjects, RecursiveTreeObject::getChildren);
		pastValuesTreeView.setSelectionModel(null);
		pastValuesTreeView.getColumns().setAll(Arrays.asList(packetId, packetDate, samplingRate, visualize));
		pastValuesTreeView.setRoot(root);
		pastValuesTreeView.setShowRoot(false);
	}

	private void openDialog(String message) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/view/DialogPopUpLayout.fxml"));
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
		} catch (IOException error) {
			error.printStackTrace();
		}
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
