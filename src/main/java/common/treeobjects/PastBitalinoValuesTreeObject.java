package common.treeobjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;

public class PastBitalinoValuesTreeObject extends RecursiveTreeObject<PastBitalinoValuesTreeObject> {

	private StringProperty treePacketid;
	private StringProperty treePacketdate;
	private StringProperty treeSamplingRate;
	
	private ObjectProperty<JFXButton> viewRecord;
	
	public PastBitalinoValuesTreeObject(int packetid, String packetDate, int samplingRate) {
				
		this.treePacketid = new SimpleStringProperty("" + packetid);
		this.treePacketdate = new SimpleStringProperty(packetDate);
		this.treeSamplingRate = new SimpleStringProperty("" + samplingRate);
		
		JFXButton viewRecord = new JFXButton("View");
		viewRecord.getStyleClass().add("tree_table_button");
		viewRecord.setOnAction((ActionEvent event) -> {
			viewRecord();
		});
		
		this.viewRecord = new SimpleObjectProperty<JFXButton>(viewRecord);
	}
	
	private void viewRecord() {
		//TODO implement view past records
		
		//ECGdataSet.clearData();
		//ECGdataSet.add(xValues, yValues);
		//EMGdataSet.clearData();
		//EMGdataSet.add(xValues2, yValues2);
	}
	
	public StringProperty getTreePacketid() {
		return treePacketid;
	}

	public void setTreePacketid(StringProperty treePacketid) {
		this.treePacketid = treePacketid;
	}

	public StringProperty getTreePacketdate() {
		return treePacketdate;
	}

	public void setTreePacketdate(StringProperty treePacketdate) {
		this.treePacketdate = treePacketdate;
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
