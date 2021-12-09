package bitalinoConnectionPane;

import java.util.ArrayList;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;

import BITalino.BITalino;

public class BitalinoConnection {
	
	public BitalinoConnection() {}

	public ArrayList<String> getBitalinosMACs() throws InterruptedException, BluetoothStateException {
		
		Vector<RemoteDevice> availableDevices = new Vector<RemoteDevice>();
		ArrayList<String> MACsList = new ArrayList<String>();
		BITalino bitalino = new BITalino();
		availableDevices = bitalino.findDevices();
		
		
		// Adds the : to the MAC address to show it in the UI 
		for(RemoteDevice device: availableDevices) {
			String MACAddress = device.getBluetoothAddress();
			MACsList.add("" + MACAddress.charAt(0) + MACAddress.charAt(1) + ":" + MACAddress.charAt(2) + MACAddress.charAt(3) + ":" +
					MACAddress.charAt(4) + MACAddress.charAt(5) + ":" + MACAddress.charAt(6) + MACAddress.charAt(7) + ":" + 
					MACAddress.charAt(8) + MACAddress.charAt(9) + ":" + MACAddress.charAt(10) + MACAddress.charAt(11));
		}
		return MACsList;
	}
}
