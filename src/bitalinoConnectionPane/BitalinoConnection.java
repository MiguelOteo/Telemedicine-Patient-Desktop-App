package bitalinoConnectionPane;

import java.util.Vector;

import javax.bluetooth.RemoteDevice;

import BITalino.BITalino;
import BITalino.DeviceDiscoverer;

public class BitalinoConnection {
	
	public void getBitalinosMACs() throws InterruptedException{
		BITalino.findDevices();
		
	}
}
