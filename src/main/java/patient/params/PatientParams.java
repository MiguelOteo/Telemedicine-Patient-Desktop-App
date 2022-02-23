package patient.params;

public @interface PatientParams {

	// RestAPI connection URL
	public static final String BASE_URL = "http://localhost:8080/TelemedicineRestAPI";

	// Regex valid letters for DNI (ID)
	public static final String DNI_LETTERS = "[T,R,W,A,G,M,Y,F,P,X,B,N,J,Z,S,Q,V,H,L,C,K,E]";

	// Parameters for BITalino record and display data on graphs
	public static final int BLOCK_SIZE = 1000;
	public static final int SAMPLING_RATE = 100;
	
	// Main app icon root
	public static final String APP_ICON = "/icons/patient-app-icon.png";
	
	// FXML files roots
	public static final String LOG_IN_VIEW = "/patient/view/LogInLayout.fxml";
	public static final String REGISTRATION_VIEW = "/patient/view/RegistrationLayout.fxml";
	public static final String INSERT_ID_VIEW = "/patient/view/InsertIdLayout.fxml";
	public static final String DIALOG_POP_UP_VIEW = "/patient/view/DialogPopUpLayout.fxml";	
	public static final String PATIENT_MENU_VIEW = "/patient/view/PatientMenuLayout.fxml";
	public static final String PATIENT_ACCOUNT_VIEW = "/patient/view/PatientAccountLayout.fxml";
	public static final String PARAMETERS_RECORD_VIEW = "/patient/view/ParametersRecordLayout.fxml";
	public static final String BITALINO_CONNECTED_VIEW = "/patient/view/BitalinoConnectedLayout.fxml";
	public static final String BITALINO_CONNECTION_VIEW = "/patient/view/BitalinoConnectionLayout.fxml";
	public static final String PATIENT_MESSENGER_VIEW = "/patient/view/PatientMessengerLayout.fxml";
}
