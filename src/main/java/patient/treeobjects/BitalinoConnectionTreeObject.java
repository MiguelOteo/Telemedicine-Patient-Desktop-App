package patient.treeobjects;

import java.io.IOException;
import java.util.Objects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import patient.communication.AccountObjectCommunication;
import patient.params.PatientParams;

public class BitalinoConnectionTreeObject extends RecursiveTreeObject<BitalinoConnectionTreeObject> {

	private final Pane pane;
	private final String MACAddress;
	
	private final StringProperty bitalinoName;
	private final StringProperty bitalinoMAC;
	private final ObjectProperty<JFXButton> establishConnection;
	
	public BitalinoConnectionTreeObject(Pane pane, String name, String MACAddress) {
		
		this.MACAddress = MACAddress;
		this.pane = pane;
		
		this.bitalinoName = new SimpleStringProperty(name);
		this.bitalinoMAC = new SimpleStringProperty(MACAddress);
		
		JFXButton establishConnection = new JFXButton("Select");
		establishConnection.getStyleClass().add("tree_table_button");
		establishConnection.setOnAction((ActionEvent event) -> establishConnection());
		
		this.establishConnection = new SimpleObjectProperty<>(establishConnection);
	}
	
	private void establishConnection() {
		AccountObjectCommunication.setMAC(MACAddress);
		AccountObjectCommunication.getButtonControl1().setDisable(false);
		loadConnectedPane();
	}
	
	private void loadConnectedPane() {
		Pane bitalinoConnectedPane;
		try {
			bitalinoConnectedPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(PatientParams.BITALINO_CONNECTED_VIEW)));
			pane.getChildren().removeAll();
			pane.getChildren().setAll(bitalinoConnectedPane);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	public StringProperty getBitalinoName() {return bitalinoName;}

	public StringProperty getBitalinoMAC() {return bitalinoMAC;}

	public ObjectProperty<JFXButton> getEstablishConnection() {return establishConnection;}

	public String getMACAddress() {return MACAddress;}
}
