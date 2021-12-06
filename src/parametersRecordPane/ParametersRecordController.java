package parametersRecordPane;

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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import BITalino.BITalino;
import BITalino.BITalinoException;
import BITalino.Frame;
import commonParams.CommonParams;
import communication.AccountObjectCommunication;
import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.ui.geometry.Side;
import de.gsi.dataset.spi.DoubleDataSet;
import dialogPopUp.DialogPopUpController;
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
import javafx.scene.control.MenuButton;
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
import models.APIRequest;
import models.APIResponse;
import models.BitalinoPackage;
import treeTableObjects.PastBitalinoValuesTreeObject;


public class ParametersRecordController implements Initializable {

	private boolean isECG = true;
	
	private boolean recordvalue = false;
	
	@SuppressWarnings("unused")
	private int counter = 0;
	
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
	
	private List<String> packetIds = new ArrayList<String>(); 
	
	private List<String> packetDates = new ArrayList<String>(); 
	
	private int idvalue = 0;
	
	private int SamplingRate = 100;
	
	private XYChart dataChart;
	
	private final DoubleDataSet ECGdataSet = new DoubleDataSet("ECG Records");
	
    @SuppressWarnings("unused")
	private final DoubleDataSet EMGdataSet = new DoubleDataSet("EMG Records");
    
    private DefaultNumericAxis xAxis = new DefaultNumericAxis("Time", "Seconds");
    
    private DefaultNumericAxis yAxis = new DefaultNumericAxis("Records", "mV");
    
    private int lastgraphvalue = 0;
    
	@SuppressWarnings("unused")
	private final int N_SAMPLES = 60000;
	
	private int xvaluesSize = 0;
	
	private int yvaluesSize = 0;
	
	private int block_size=1000;
	
	private BITalino bitalino = null;
	
	private final double[] xValues = new double[block_size];
	
    private final double[] yValues = new double[block_size];
    
	private final double[] xValues2 = new double[block_size];
	
    private final double[] yValues2 = new double[block_size];
    
    private String MAC;
	
	public void initialize(URL location, ResourceBundle resources) {
			loadTreeTable();
			xAxis.setSide(Side.BOTTOM);
			yAxis.setSide(Side.LEFT);
			dataChart = new XYChart(xAxis, yAxis);
			final Zoomer zoom = new Zoomer();
			zoom.omitAxisZoomList().add(yAxis);
			zoom.setSliderVisible(false);
			dataChart.getPlugins().add(zoom);
			viewPane.getChildren().add(dataChart);
			xAxis.setForceZeroInRange(false);
			yAxis.setForceZeroInRange(false);
		    ECGdataSet.resize(xvaluesSize);
		    changegraph.setVisible(false);
	        MAC = AccountObjectCommunication.getMAC();
	        macLabel.setText(MAC);
	        pastValuesTreeView.setPlaceholder(new Label("No data available to show"));
			
	}
	
	@FXML
	private void startStopRecording(MouseEvent event) {
		lastgraphvalue = 0;
		if (recordvalue == true) {
			recordvalue = false;
			nothingtoshow.setText("Recording has stopped");
		}
		else {
			recordvalue = true;
		}

		 pastValuesTreeView.refresh();
		 int patientId = AccountObjectCommunication.getPatient().getPatientId();
		 
         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
		nothingtoshow.setText("Recording has started, please wait");
        MAC = AccountObjectCommunication.getMAC();
	    changegraph.setVisible(true);
		changegraph.setDisable(true);
		startRecording.setDisable(true);
 		Thread threadObject = new Thread("Recording BITalino data") {
			public void run() {
         
	        try {
	            bitalino = new BITalino();

	            
	            //Sampling rate, should be 10, 100 or 1000
	            //int SamplingRate = 10;

	            bitalino.open(MAC, SamplingRate);

	            // Start acquisition on analog channels A1 and A2
	            // For example, If you want A1, A3 and A4 you should use {0,2,3}
	            int[] channelsToAcquire = {0, 1};
	            bitalino.start(channelsToAcquire);

	            //Read in total 10000000 times
	            //while with boolean that button changes so that it closes	
	            for(int k = 0; k <1 ; k++) {

	                //Each time read a block of 10 samples 

	                frame = bitalino.read(block_size);
	                //LocalDateTime now = LocalDateTime.now();  
	                Timestamp now = new Timestamp(System.currentTimeMillis());
	                
	                String strDate = dateFormat.format(now);
	                //System.out.println("size block: " + frame.length);
	                
	                
	                //kk id identification
	                packetIds.add(Integer.toString(idvalue));
	                idvalue++;
	                packetDates.add(strDate);
	                String emgValues = "[";
	                String ecgValues = "[";
	                for (int i = 0; i < frame.length; i++) {
	                	
	                	emgValues = emgValues + frame[i].analog[0]+ ","; 
	                	ecgValues = ecgValues + frame[i].analog[1] + ",";
	                	


	                }
	                emgValues = emgValues.substring(0, emgValues.length() - 1);
	                ecgValues = ecgValues.substring(0, ecgValues.length() - 1);
	                emgValues = emgValues + "]";
	                ecgValues = ecgValues + "]";
	                System.out.println(emgValues);
	                System.out.println(ecgValues);
	                BitalinoPackage bitalinopack = new BitalinoPackage(patientId, SamplingRate, now, emgValues, ecgValues);
	                AccountObjectCommunication.getPatient().addNewPackage(bitalinopack);
	                
	                try {
						HttpURLConnection connection = (HttpURLConnection) new URL(CommonParams.BASE_URL + "/addPacketsToPatient")
								.openConnection();
						
						connection.setRequestMethod("POST");
						
						Gson gsonConverter = new Gson();
						APIRequest requestAPI = new APIRequest();
						requestAPI.setBitalinopackage(bitalinopack);
						
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
						
						if (!responseAPI.isError()){
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									openDialog(responseAPI.getAPImessage());

								}
							});
						}
						else{
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
								openDialog("Failed to connect to the server");
								conncetionError.printStackTrace();
							}
						});
					} catch (IOException error) {
						error.printStackTrace();
					}
				
	                
	                
	                //write the file to send
	                
	                //String nombrepaquete = "paquete " + counter;
	                //counter++;
	                //FileWriter fileWriter = new FileWriter(nombrepaquete);
	                //fileWriter.write(new Gson().toJson(frame));
	                //fileWriter.close();
	                loadData();
	                pastValuesTreeView.refresh();

			    	String graphecg = bitalinopack.getecgData();
			    	graphecg = graphecg.substring(0, graphecg.length() - 1); //delete last ]
			    	graphecg = graphecg.substring(1, graphecg.length());  //delete first [
			    	ArrayList<String> graphecglist = new ArrayList<>(Arrays.asList(graphecg.split(",")));
			    	
			    	String graphemg = bitalinopack.getemgData();
			    	graphemg = graphemg.substring(0, graphemg.length() - 1); //delete last ]
			    	graphemg = graphemg.substring(1, graphemg.length());  //delete first [
			    	ArrayList<String> graphemglist = new ArrayList<>(Arrays.asList(graphemg.split(",")));
			    	
				    for (int n = 0; n < block_size; n++) {
				    	xValues[n+lastgraphvalue] =n+lastgraphvalue;
				    	//double yvalue = Double.parseDouble(graphecglist.get(n)); 
				       yValues[n+lastgraphvalue] = Integer.parseInt(graphecglist.get(n));
				    }
				    
				    ECGdataSet.clearData();
				    ECGdataSet.add(xValues, yValues);
				    
					
				    for (int n = 0; n < block_size; n++) {
				    	xValues2[n+lastgraphvalue] =n+lastgraphvalue;
				    	//double yvalue = Double.parseDouble(graphecglist.get(n)); 
				       yValues2[n+lastgraphvalue] = Integer.parseInt(graphemglist.get(n));;
				    }
				    lastgraphvalue = block_size+lastgraphvalue;
				    EMGdataSet.clearData();
				    EMGdataSet.add(xValues2, yValues2);

	                
	                
	            }//for loop
	            //stop acquisition
	            bitalino.stop();
	        } catch (BITalinoException ex) {
	            Logger.getLogger(ParametersRecordController.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (Throwable ex) {
	            Logger.getLogger(ParametersRecordController.class.getName()).log(Level.SEVERE, null, ex);
	        } finally {
	            try {
	                //close bluetooth connection
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
		
	    dataChart.getDatasets().clear();
	    dataChart.getDatasets().add(ECGdataSet);
	    startRecording.setDisable(false);
		changegraph.setDisable(false);
    	recordvalue = false;
		nothingtoshow.setText("Recording has stopped");
		isECG = true;

	}
		
		/*la idea es  usar este boton para empezar a grabar y parar
		 * dependiendo de una variable hará una cosa o la otra, cambiando a su vez el texto del boton
		 * 
		 * Se crea una lista
		 */
	
	@FXML
	private void changeChart(MouseEvent event) {

		if (isECG) { // If true then ECG graph has to be change

			isECG = false;
			changegraph.setText("Show ECG Recording");
			dataChart.getDatasets().clear();
			dataChart.getDatasets().add(EMGdataSet);

		} else {

			isECG = true;
			changegraph.setText("Show EMG Recording");
			dataChart.getDatasets().clear();
			dataChart.getDatasets().add(ECGdataSet);
		}
	}
	private void loadData() {
		int count = 0;
		String rate = Integer.toString(SamplingRate);
		recordsObjects.clear();
		for (String packetIds: packetIds) {
			String date = packetDates.get(count);
			recordsObjects.add(new PastBitalinoValuesTreeObject(mainPane, packetIds, date, rate));
			count++;
		}
		pastValuesTreeView.refresh();
	}
	
	private void loadTreeTable() {
		
		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> packetId = new JFXTreeTableColumn<>("Packet ID");
		//bitalinoName.setPrefWidth(155);
		packetId.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PastBitalinoValuesTreeObject, String> param) {
				return param.getValue().getValue().getTreePacketid();
			}
		});
		packetId.setResizable(false);
		
		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> packetDate = new JFXTreeTableColumn<>("Date of Recording");
		packetDate.setPrefWidth(160);
		packetDate.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PastBitalinoValuesTreeObject, String> param) {
				return param.getValue().getValue().getTreePacketdate();
			}
		});
		packetDate.setResizable(false);
		
		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> samplingRate = new JFXTreeTableColumn<>("Sampling Rate");
		samplingRate.setPrefWidth(140);
		samplingRate.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PastBitalinoValuesTreeObject, String> param) {
				return param.getValue().getValue().getTreeSamplingRate();
			}
		});
		samplingRate.setResizable(false);
		
		JFXTreeTableColumn<PastBitalinoValuesTreeObject, JFXButton> visualize = new JFXTreeTableColumn<>("Visualize");
		//establishConnection.setPrefWidth(165);
		visualize.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject, JFXButton>, ObservableValue<JFXButton>>() {
			@Override
			public ObservableValue<JFXButton> call(CellDataFeatures<PastBitalinoValuesTreeObject, JFXButton> param) {
				return param.getValue().getValue().getViewRecord();
			}
		});
		visualize.setResizable(false);
		
		TreeItem<PastBitalinoValuesTreeObject> root = new RecursiveTreeItem<PastBitalinoValuesTreeObject>(recordsObjects, RecursiveTreeObject::getChildren);
		pastValuesTreeView.setSelectionModel(null);
		pastValuesTreeView.getColumns().setAll(Arrays.asList(packetId, packetDate, samplingRate, visualize));
		pastValuesTreeView.setRoot(root);
		pastValuesTreeView.setShowRoot(false);
	}
	
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

