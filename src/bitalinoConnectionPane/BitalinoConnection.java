package bitalinoConnectionPane;

import java.util.ArrayList;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;

import BITalino.BITalino;

public class BitalinoConnection {
	
	public BitalinoConnection() {}

	public ArrayList<String> getBitalinosMACs() throws InterruptedException {
		Vector<RemoteDevice> availableDevices = new Vector<RemoteDevice>();
		ArrayList<String> MACsList = new ArrayList<String>();
		String tmp = "";
		String tmp2 = "";
		availableDevices = BITalino.findDevices();
		
		System.out.println(availableDevices);
		for (int i = 0; i < availableDevices.size(); i++) {
			tmp = availableDevices.elementAt(i).toString();
			for (int j = 0; j < tmp.length(); j++) {
				tmp2 = tmp2 + tmp.charAt(j);
				if (j % 2 != 0) {
					if (j != (tmp.length() - 1)) {
						tmp2 = tmp2 + ":";
					}
				}
			}
			MACsList.add(tmp2);
			tmp = "";
			tmp2 = "";
		}
		return MACsList;
	}

	public String getBitalinoMacFromAvailableBitalinosList(int num, ArrayList<String> MACsList) {
		String mac = "error";
		if (num >= MACsList.size()) {
			mac = "Error";
		} else {
			for (int i = 0; i < MACsList.size(); i++) {
				mac = MACsList.get(num);
			}
		}
		return mac;
	}
}
