package patient.params;

public @interface PatientParams {

	// RestAPI connection URL
	String BASE_URL = "http://localhost:8080/TelemedicineRestAPI";

	// Regex valid letters for DNI (ID)
	String DNI_LETTERS = "[T,R,W,A,G,M,Y,F,P,X,B,N,J,Z,S,Q,V,H,L,C,K,E]";

	// Parameters for BITalino record and display data on graphs
	int BLOCK_SIZE = 1000;
	int SAMPLING_RATE = 100;
	
	// Main app icon root
	String APP_ICON = "/icons/patient-app-icon.png";
	
	// FXML files roots
	String LOG_IN_VIEW = "/patient/view/LogInLayout.fxml";
	String REGISTRATION_VIEW = "/patient/view/RegistrationLayout.fxml";
	String INSERT_ID_VIEW = "/patient/view/InsertIdLayout.fxml";
	String DIALOG_POP_UP_VIEW = "/patient/view/DialogPopUpLayout.fxml";
	String PATIENT_MENU_VIEW = "/patient/view/PatientMenuLayout.fxml";
	String PATIENT_ACCOUNT_VIEW = "/patient/view/PatientAccountLayout.fxml";
	String PARAMETERS_RECORD_VIEW = "/patient/view/ParametersRecordLayout.fxml";
	String BITALINO_CONNECTED_VIEW = "/patient/view/BitalinoConnectedLayout.fxml";
	String BITALINO_CONNECTION_VIEW = "/patient/view/BitalinoConnectionLayout.fxml";
	String PATIENT_MESSENGER_VIEW = "/patient/view/PatientMessengerLayout.fxml";
}
