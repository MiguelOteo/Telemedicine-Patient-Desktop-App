package bitalinoConnectionPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import BITalino.BITalino;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import treeTableObjects.BitalinoConnectionTreeObject;

public class BitalinoConnectionController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private JFXTreeTableView<BitalinoConnectionTreeObject> bitalinoTreeView;
	@FXML
	private final ObservableList<BitalinoConnectionTreeObject> bitalinoObjects = FXCollections.observableArrayList();
	
	private List<String> BitalinosMAC = new ArrayList<String>(); 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<String> macList =new ArrayList<String>();
		BitalinoConnection bita = new BitalinoConnection();
		try {
			macList=bita.getBitalinosMACs();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String mac: macList) {
			BitalinosMAC.add(mac);
		}
		loadTreeTable();
		loadData();
	}

	@FXML
	private void minWindow(MouseEvent event) {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
	}
	
	@FXML
	private void closeApp(MouseEvent event) {
		System.exit(0);
	}
	
	/*
	 * 	--> Tree Table View view
	 */
	
	// Loads BITalinos found around the computer and list them 
	
	private void loadData() {
		int count = 1;
		bitalinoObjects.clear();
		for (String MACAdress: BitalinosMAC) {
			bitalinoObjects.add(new BitalinoConnectionTreeObject(mainPane, "BITalino " + count, MACAdress));
			count++;
		}
		bitalinoTreeView.refresh();
	}
	
	// Establishes the list columns and loads the BITalino list
	private void loadTreeTable() {
		
		JFXTreeTableColumn<BitalinoConnectionTreeObject, String> bitalinoName = new JFXTreeTableColumn<>("BITalinos found");
		bitalinoName.setPrefWidth(155);
		bitalinoName.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<BitalinoConnectionTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<BitalinoConnectionTreeObject, String> param) {
				return param.getValue().getValue().getBitalinoName();
			}
		});
		bitalinoName.setResizable(false);
		
		JFXTreeTableColumn<BitalinoConnectionTreeObject, String> bitalinoMAC = new JFXTreeTableColumn<>("BITalinos M.A.C Addresses");
		bitalinoMAC.setPrefWidth(200);
		bitalinoMAC.setCellValueFactory(new Callback<JFXTreeTableColumn.CellDataFeatures<BitalinoConnectionTreeObject,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<BitalinoConnectionTreeObject, String> param) {
				return param.getValue().getValue().getBitalinoMAC();
			}
		});
		bitalinoMAC.setResizable(false);
		
		JFXTreeTableColumn<BitalinoConnectionTreeObject, JFXButton> establishConnection = new JFXTreeTableColumn<>("Establish Connection");
		establishConnection.setPrefWidth(165);
		establishConnection.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<BitalinoConnectionTreeObject, JFXButton>, ObservableValue<JFXButton>>() {
			@Override
			public ObservableValue<JFXButton> call(CellDataFeatures<BitalinoConnectionTreeObject, JFXButton> param) {
				return param.getValue().getValue().getStablishConnection();
			}
		});
		establishConnection.setResizable(false);
		
		TreeItem<BitalinoConnectionTreeObject> root = new RecursiveTreeItem<BitalinoConnectionTreeObject>(bitalinoObjects, RecursiveTreeObject::getChildren);
		bitalinoTreeView.setSelectionModel(null);
		bitalinoTreeView.setPlaceholder(new Label("No BITalino found around you"));
		bitalinoTreeView.getColumns().setAll(Arrays.asList(bitalinoName, bitalinoMAC, establishConnection));
		bitalinoTreeView.setRoot(root);
		bitalinoTreeView.setShowRoot(false);
	}
}
