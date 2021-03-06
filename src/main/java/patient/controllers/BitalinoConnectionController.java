package patient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import patient.bitalino.BitalinoConnection;
import patient.communication.AccountObjectCommunication;
import patient.params.PatientParams;
import patient.treeobjects.BitalinoConnectionTreeObject;

import javax.bluetooth.BluetoothStateException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static patient.params.PatientParams.*;

public class BitalinoConnectionController implements Initializable {

	@FXML
	private Pane mainPane;
	@FXML
	private JFXButton refreshButton;
	@FXML
	private JFXTreeTableView<BitalinoConnectionTreeObject> bitalinoTreeView;
	@FXML
	private final ObservableList<BitalinoConnectionTreeObject> bitalinoObjects = FXCollections.observableArrayList();
	
	private final List<String> bitalinosMAC = new ArrayList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadTreeTable();
		searchBitalinos();
	}

	@FXML
	private void minWindow() {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		stage.setIconified(true);
	}
	
	@FXML
	private void closeApp() {
		System.exit(0);
	}
	
	@FXML
	private void refreshFinder() {
		bitalinosMAC.clear();
		bitalinoObjects.clear();
		bitalinoTreeView.refresh();
		AccountObjectCommunication.getButtonControl1().setDisable(true);
		searchBitalinos();
	}
	
	private void searchBitalinos() {
		refreshButton.setDisable(true);
		bitalinoTreeView.setPlaceholder(new Label("Searching for BITalinos, wait a few seconds"));
		Thread threadObject = new Thread("FindingBITalinos") {
			public void run() {
				
				ArrayList<String> macList;
				BitalinoConnection bita = new BitalinoConnection();
				try {
					macList = bita.getBitalinosMACs();
					bitalinosMAC.addAll(macList);

					Platform.runLater(() -> {
						bitalinoTreeView.setPlaceholder(new Label("No BITalinos found around you"));
						refreshButton.setDisable(false);
						loadData();
					});
					
				} catch (BluetoothStateException bluetoothOFF) {
					
					Platform.runLater(() -> {
						if(bluetoothOFF.getMessage().equals("Another inquiry already running")) {
							bitalinoTreeView.setPlaceholder(new Label("Press refresh button"));
							openDialog("Wait for the search of BITalinos to finish");
						}  else {
							bitalinoTreeView.setPlaceholder(new Label("Bluetooth is turned off"));
							openDialog("Remember to turn on the Bluetooth");
						}
						refreshButton.setDisable(false);
					});
				} catch (InterruptedException exception) {
					Platform.runLater(() -> {
						bitalinoTreeView.setPlaceholder(new Label("Error occurred searching for BITalinos"));
						openDialog("Error occurred searching for BITalinos");
						refreshButton.setDisable(false);
						exception.printStackTrace();
					});
				}
			}
		};
		threadObject.start();
	}
	
	// Displays any error message on a popUp pane
	private void openDialog(String message) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(DIALOG_POP_UP_VIEW));
			Parent root = loader.load();
			DialogPopUpController controller = loader.getController();
			controller.setMessage(message);
			Stage stage = new Stage();
			stage.setHeight(130);
			stage.setWidth(300);
			Scene scene = new Scene(root);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Telelepsia Message");
			stage.getIcons().add(new Image(APP_ICON));
			
			// Set the pop-up in the center of the main menu window
			stage.setX(LogInController.getStage().getX() + LogInController.getStage().getWidth() / 2 - stage.getWidth() / 2);
			stage.setY(-75 + LogInController.getStage().getY() + LogInController.getStage().getHeight() / 2 - stage.getHeight() / 2);
			
			AccountObjectCommunication.getAnchorPane().setEffect(new BoxBlur(4, 4, 4));
			stage.show();
			stage.setOnHiding(event -> AccountObjectCommunication.getAnchorPane().setEffect(null));
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
	
	/*
	 * 	--> Tree Table View view
	 */
	
	// Loads BITalinos found around the computer and list them 
	
	private void loadData() {
		int count = 1;
		bitalinoObjects.clear();
		for (String MACAddress: bitalinosMAC) {
			bitalinoObjects.add(new BitalinoConnectionTreeObject(mainPane, "BITalino " + count, MACAddress));
			count++;
		}
		bitalinoTreeView.refresh();
	}
	
	// Establishes the list columns and loads the BITalino list
	private void loadTreeTable() {
		
		JFXTreeTableColumn<BitalinoConnectionTreeObject, String> bitalinoName = new JFXTreeTableColumn<>("BITalinos found");
		bitalinoName.setPrefWidth(155);
		bitalinoName.setCellValueFactory(param -> param.getValue().getValue().getBitalinoName());
		bitalinoName.setResizable(false);
		
		JFXTreeTableColumn<BitalinoConnectionTreeObject, String> bitalinoMAC = new JFXTreeTableColumn<>("BITalinos M.A.C Addresses");
		bitalinoMAC.setPrefWidth(200);
		bitalinoMAC.setCellValueFactory(param -> param.getValue().getValue().getBitalinoMAC());
		bitalinoMAC.setResizable(false);
		
		JFXTreeTableColumn<BitalinoConnectionTreeObject, JFXButton> establishConnection = new JFXTreeTableColumn<>("Establish Connection");
		establishConnection.setPrefWidth(165);
		establishConnection.setCellValueFactory(param -> param.getValue().getValue().getEstablishConnection());
		establishConnection.setResizable(false);
		
		TreeItem<BitalinoConnectionTreeObject> root = new RecursiveTreeItem<>(bitalinoObjects, RecursiveTreeObject::getChildren);
		bitalinoTreeView.setSelectionModel(null);
		bitalinoTreeView.getColumns().setAll(Arrays.asList(bitalinoName, bitalinoMAC, establishConnection));
		bitalinoTreeView.setRoot(root);
		bitalinoTreeView.setShowRoot(false);
	}
}
