package treeTableObjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import de.gsi.dataset.spi.DoubleDataSet;
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
	private double[] xValues = new double[1000];
	private double[] yValues = new double[1000];
	private double[] xValues2 = new double[1000];
	private double[] yValues2 = new double[1000];
	private  DoubleDataSet ECGdataSet;
	
	private DoubleDataSet EMGdataSet;
	
	private StringProperty treePacketid;
	private StringProperty treePacketdate;
	private StringProperty treeSamplingRate;
	
	private ObjectProperty<JFXButton> viewRecord;
	
	public PastBitalinoValuesTreeObject(Pane pane, String packetid, String packetdate, String samplingRate, double[] xValues, double[] yValues, double[] xValues2, double[] yValues2, DoubleDataSet EMGdataSet, DoubleDataSet ECGdataSet) {
		
		this.packetid = packetid;
		this.packetdate = packetid;
		this.samplingRate = samplingRate;
		this.pane = pane;
		this.xValues = xValues;
		this.yValues = yValues;
		this.xValues2 = xValues2;
		this.yValues2 = yValues2;
		this.ECGdataSet = ECGdataSet;
		this.EMGdataSet = EMGdataSet;
		
		this.treePacketid = new SimpleStringProperty(packetid);
		this.treePacketdate = new SimpleStringProperty(packetdate);
		this.treeSamplingRate = new SimpleStringProperty(samplingRate);
		
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

	public double[] getxValues() {
		return xValues;
	}

	public void setxValues(double[] xValues) {
		this.xValues = xValues;
	}

	public double[] getyValues() {
		return yValues;
	}

	public void setyValues(double[] yValues) {
		this.yValues = yValues;
	}

	public double[] getxValues2() {
		return xValues2;
	}

	public void setxValues2(double[] xValues2) {
		this.xValues2 = xValues2;
	}

	public double[] getyValues2() {
		return yValues2;
	}

	public void setyValues2(double[] yValues2) {
		this.yValues2 = yValues2;
	}
}
