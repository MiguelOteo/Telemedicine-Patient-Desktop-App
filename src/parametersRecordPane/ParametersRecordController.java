package parametersRecordPane;

import java.io.FileWriter;
import java.net.URL;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	@FXML
	private void startRecording() {
		
		if (recordvalue == true){
			recordvalue = false;
			 BITalino bitalino = null;
		        try {
		            bitalino = new BITalino();
		            // Code to find Devices
		            //Only works on some OS
		            Vector<RemoteDevice> devices = bitalino.findDevices();
		            System.out.println(devices);

		            //You need TO CHANGE THE MAC ADDRESS
		            //You should have the MAC ADDRESS in a sticker in the Bitalino
		            
		            //Sampling rate, should be 10, 100 or 1000
		            int SamplingRate = 10;
		            String MAC = AccountObjectCommunication.getMAC();
		            bitalino.open(MAC, SamplingRate);

		            // Start acquisition on analog channels A2 and A6
		            // For example, If you want A1, A3 and A4 you should use {0,2,3}
		            int[] channelsToAcquire = {1, 5};
		            bitalino.start(channelsToAcquire);

		            //Read in total 10000000 times
		            for (int j = 0; j < 10000000; j++) {

		                //Each time read a block of 10 samples 
		                int block_size=10;
		                frame = bitalino.read(block_size);

		                System.out.println("size block: " + frame.length);

		                //Print the samples
		                for (int i = 0; i < frame.length; i++) {
		                    System.out.println((j * block_size + i) + " seq: " + frame[i].seq + " "
		                            + frame[i].analog[0] + " "
		                            + frame[i].analog[1] + " "
		                    //  + frame[i].analog[2] + " "
		                    //  + frame[i].analog[3] + " "
		                    //  + frame[i].analog[4] + " "
		                    //  + frame[i].analog[5]
		                    );

		                }
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
			//start recording
			//bucle
		else {
			//end recording
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

