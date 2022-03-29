package patient.treeobjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;

public class PastBitalinoValuesTreeObject extends RecursiveTreeObject<PastBitalinoValuesTreeObject> {

	private StringProperty treePacketID;
	private StringProperty treePacketDate;
	private StringProperty treeSamplingRate;

	private ObjectProperty<JFXButton> viewRecord;
	
	public PastBitalinoValuesTreeObject(int packetID, String packetDate, int samplingRate) {
				
		this.treePacketID = new SimpleStringProperty("" + packetID);
		this.treePacketDate = new SimpleStringProperty(packetDate);
		this.treeSamplingRate = new SimpleStringProperty("" + samplingRate);
		
		JFXButton viewRecord = new JFXButton("View");
		viewRecord.getStyleClass().add("tree_table_button");
		viewRecord.setOnAction((ActionEvent event) -> {
			viewRecord();
		});
		
		this.viewRecord = new SimpleObjectProperty<JFXButton>(viewRecord);
	}
	
	private void viewRecord() {

		//TODO - implement view past records
		
		//ECGdataSet.clearData();
		//ECGdataSet.add(xValues, yValues);
		//EMGdataSet.clearData();
		//EMGdataSet.add(xValues2, yValues2);
	}
	
	public StringProperty getTreePacketID() {
		return treePacketID;
	}

	public void setTreePacketID(StringProperty treePacketID) {
		this.treePacketID = treePacketID;
	}

	public StringProperty getTreePacketDate() {
		return treePacketDate;
	}

	public void setTreePacketDate(StringProperty treePacketDate) {
		this.treePacketDate = treePacketDate;
	}

	public StringProperty getTreeSamplingRate() {
		return treeSamplingRate;
	}

	public void setTreeSamplingRate(StringProperty treeSamplingRate) {
		this.treeSamplingRate = treeSamplingRate;
	}

	public ObjectProperty<JFXButton> getViewRecord() {
		return viewRecord;
	}

	public void setViewRecord(ObjectProperty<JFXButton> viewRecord) {
		this.viewRecord = viewRecord;
	}
}
