package treeTableObjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;

public class PastBitalinoValuesTreeObject extends RecursiveTreeObject<PastBitalinoValuesTreeObject> {

	@SuppressWarnings("unused")
	private Pane pane;
	private String packetid;
	private String packetdate;
	private String samplingRate;
	
	
	private StringProperty treePacketid;
	private StringProperty treePacketdate;
	private StringProperty treeSamplingRate;
	
	private ObjectProperty<JFXButton> viewRecord;
	
	public PastBitalinoValuesTreeObject(Pane pane, String packetid, String packetdate, String samplingRate) {
		
		this.packetid = packetid;
		this.packetdate = packetid;
		this.samplingRate = samplingRate;
		this.pane = pane;
		
		this.treePacketid = new SimpleStringProperty(packetid);
		this.treePacketdate = new SimpleStringProperty(packetid);
		this.treeSamplingRate = new SimpleStringProperty(samplingRate);
		
		JFXButton viewRecord = new JFXButton("View");
		viewRecord.getStyleClass().add("tree_table_button");
		viewRecord.setOnAction((ActionEvent event) -> {
			viewRecord();
		});
		
		this.viewRecord = new SimpleObjectProperty<JFXButton>(viewRecord);
	}
	
	private void viewRecord() {

	}
	

	public String getPacketid() {
		return packetid;
	}

	public void setPacketid(String packetid) {
		this.packetid = packetid;
	}

	public String getPacketdate() {
		return packetdate;
	}

	public void setPacketdate(String packetdate) {
		this.packetdate = packetdate;
	}

	public String getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(String samplingRate) {
		this.samplingRate = samplingRate;
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
