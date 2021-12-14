package main.java.common.treeobjects;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import main.java.common.communication.AccountObjectCommunication;

public class BitalinoConnectionTreeObject extends RecursiveTreeObject<BitalinoConnectionTreeObject> {

	private Pane pane;
	private String MACAddress;
	
	private StringProperty bitalinoName;
	private StringProperty bitalinoMAC;
	private ObjectProperty<JFXButton> stablishConnection;
	
	public BitalinoConnectionTreeObject(Pane pane, String name, String MACAddress) {
		
		this.MACAddress = MACAddress;
		this.pane = pane;
		
		this.bitalinoName = new SimpleStringProperty(name);
		this.bitalinoMAC = new SimpleStringProperty(MACAddress);
		
		JFXButton stablishConnection = new JFXButton("Select");
		stablishConnection.getStyleClass().add("tree_table_button");
		stablishConnection.setOnAction((ActionEvent event) -> {
			stablishConnection();
		});
		
		this.stablishConnection = new SimpleObjectProperty<JFXButton>(stablishConnection);
	}
	
	private void stablishConnection() {
		AccountObjectCommunication.setMAC(MACAddress);
		AccountObjectCommunication.getButtonControl1().setDisable(false);
		loadConnectedPane();
	}
	
	private void loadConnectedPane() {
		Pane bitalinoConnectedPane;
		try {
			bitalinoConnectedPane = FXMLLoader.load(getClass().getResource("/patient/view/BitalinoConnectedLayout.fxml"));
			pane.getChildren().removeAll();
			pane.getChildren().setAll(bitalinoConnectedPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	public StringProperty getBitalinoName() {return bitalinoName;}

	public StringProperty getBitalinoMAC() {return bitalinoMAC;}

	public ObjectProperty<JFXButton> getStablishConnection() {return stablishConnection;}

	public String getMACAddress() {return MACAddress;}
}
