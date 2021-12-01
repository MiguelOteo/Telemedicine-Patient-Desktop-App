package parametersRecordPane;

import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import BITalino.BITalino;
import BITalino.BITalinoException;
import BITalino.Frame;
import communication.AccountObjectCommunication;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.BitalinoPackage;
import treeTableObjects.PastBitalinoValuesTreeObject;


public class ParametersRecordController implements Initializable {

	
	private boolean recordvalue = true;
	
	private int counter = 0;
	
    private static Frame[] frame;
	@FXML
	private Pane mainPane;
	@FXML
	private Pane viewPane;
	@FXML
	private Label nothingtoshow;
	@FXML
	private JFXButton startRecording;
	@FXML
	private JFXButton comparePast;
	@FXML
	private MenuButton channelSelectButton;
	@FXML
	private JFXTreeTableView<PastBitalinoValuesTreeObject> pastValuesTreeView;
	@FXML
	private final ObservableList<PastBitalinoValuesTreeObject> recordsObjects = FXCollections.observableArrayList();
	
	private List<String> packetIds = new ArrayList<String>(); 
	
	private List<String> packetDates = new ArrayList<String>(); 
	
	private int idvalue = 0;
	
	private int SamplingRate = 10;
	
	public void initialize(URL location, ResourceBundle resources) {
			
			 pastValuesTreeView.refresh();
			 int patientId = AccountObjectCommunication.getPatient().getPatientId();
			 BITalino bitalino = null;
			 
             DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  

             
		        try {
		            bitalino = new BITalino();

		            
		            //Sampling rate, should be 10, 100 or 1000
		            //int SamplingRate = 10;
		            String MAC = AccountObjectCommunication.getMAC();
		            bitalino.open(MAC, SamplingRate);

		            // Start acquisition on analog channels A1 and A2
		            // For example, If you want A1, A3 and A4 you should use {0,2,3}
		            int[] channelsToAcquire = {0, 1};
		            bitalino.start(channelsToAcquire);

		            //Read in total 10000000 times
		            //while with boolean that button changes so that it closes	
		            while(recordvalue == true) {
		    			nothingtoshow.setText("Recording has started");
		                //Each time read a block of 10 samples 
		                int block_size=10;
		                frame = bitalino.read(block_size);
		                //LocalDateTime now = LocalDateTime.now();  
		                Timestamp now = new Timestamp(2021);
		                
		                String strDate = dateFormat.format(now);
		                //System.out.println("size block: " + frame.length);
		                
		                
		                //kk id identification
		                packetIds.add(Integer.toString(idvalue));
		                idvalue++;
		                packetDates.add(strDate);
		                pastValuesTreeView.refresh();
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
		                
		                //write the file to send
		                
		                //String nombrepaquete = "paquete " + counter;
		                //counter++;
		                //FileWriter fileWriter = new FileWriter(nombrepaquete);
		                //fileWriter.write(new Gson().toJson(frame));
		                //fileWriter.close();
		                
		                if (idvalue == 10) {
		                	recordvalue = false;
		                }
		                
		                
		            }
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
	
	@FXML
	private void startStopRecording(MouseEvent event) {
		if (recordvalue == true) {
			recordvalue = false;
			nothingtoshow.setText("Recording has stopped");
		}
		else {
			recordvalue = true;
		}
	}
		
		/*la idea es  usar este boton para empezar a grabar y parar
		 * dependiendo de una variable hará una cosa o la otra, cambiando a su vez el texto del boton
		 * 
		 * Se crea una lista
		 */
	
	@SuppressWarnings("unused")
	private void loadData() {
		int count = 1;
		String rate = Integer.toString(SamplingRate);
		recordsObjects.clear();
		for (String packetIds: packetIds) {
			recordsObjects.add(new PastBitalinoValuesTreeObject(mainPane, packetIds, "placeholder fecha", rate));
			count++;
		}
		pastValuesTreeView.refresh();
	}
	
	@SuppressWarnings("unused")
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
		//bitalinoMAC.setPrefWidth(200);
		packetDate.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<PastBitalinoValuesTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<PastBitalinoValuesTreeObject, String> param) {
				return param.getValue().getValue().getTreePacketdate();
			}
		});
		packetDate.setResizable(false);
		
		JFXTreeTableColumn<PastBitalinoValuesTreeObject, String> samplingRate = new JFXTreeTableColumn<>("Sampling Rate");
		//bitalinoMAC.setPrefWidth(200);
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

