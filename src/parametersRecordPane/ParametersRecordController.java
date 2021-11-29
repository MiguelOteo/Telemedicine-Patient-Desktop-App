package parametersRecordPane;

import java.io.FileWriter;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;

import BITalino.BITalino;
import BITalino.BITalinoException;
import BITalino.Frame;
import communication.AccountObjectCommunication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.BitalinoPackage;

import javax.bluetooth.RemoteDevice;


public class ParametersRecordController implements Initializable {

	
	private boolean recordvalue = false;
	
	private int counter = 0;
	
    private static Frame[] frame;
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
	@FXML
	private MenuButton channelSelectButton;
	
	public void initialize(URL location, ResourceBundle resources) {
		
			 int patientId = AccountObjectCommunication.getPatient().getPatientId();
			 
			 BITalino bitalino = null;
		        try {
		            bitalino = new BITalino();

		            
		            //Sampling rate, should be 10, 100 or 1000
		            int SamplingRate = 10;
		            String MAC = AccountObjectCommunication.getMAC();
		            bitalino.open(MAC, SamplingRate);

		            // Start acquisition on analog channels A1 and A2
		            // For example, If you want A1, A3 and A4 you should use {0,2,3}
		            int[] channelsToAcquire = {0, 1};
		            bitalino.start(channelsToAcquire);

		            //Read in total 10000000 times
		            //while with boolean that button changes so that it closes	
		            while(recordvalue == true) {

		                //Each time read a block of 10 samples 
		                int block_size=10;
		                frame = bitalino.read(block_size);
		                LocalDateTime now = LocalDateTime.now();  
		                System.out.println("size block: " + frame.length);
		                BitalinoPackage bitalinopack = new BitalinoPackage(patientId, SamplingRate, now, frame.toString());
		                AccountObjectCommunication.getPatient().addMeasuresDates(bitalinopack);

		            	
		                //Print the samples
		                //for (int i = 0; i < frame.length; i++) {
		                    //System.out.println(( block_size + i) + " seq: " + frame[i].seq + " "
		                            //+ frame[i].analog[0] + " "
		                            //+ frame[i].analog[1] + " "
		                    //  + frame[i].analog[2] + " "
		                    //  + frame[i].analog[3] + " "
		                    //  + frame[i].analog[4] + " "
		                    //  + frame[i].analog[5]
		                    //);

		                //}
		                //write the file to send
		                
		                String nombrepaquete = "paquete " + counter;
		                counter++;
		                FileWriter fileWriter = new FileWriter(nombrepaquete);
		                fileWriter.write(new Gson().toJson(frame));
		                fileWriter.close();
		                
		                
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
	private void startStopRecording() {
		if (recordvalue == true) {
			recordvalue = false;
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

