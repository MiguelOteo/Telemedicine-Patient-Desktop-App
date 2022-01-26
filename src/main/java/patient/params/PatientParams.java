package patient.params;

public @interface PatientParams {

	// RestAPI connection URL
	public static final String BASE_URL = "http://localhost:8080/TelemedicineRestAPI";

	// Regex valid letters for DNI (ID)
	public static final String DNI_LETTERS = "[T,R,W,A,G,M,Y,F,P,X,B,N,J,Z,S,Q,V,H,L,C,K,E]";

	// Parameters for BITalino record and display data on graphs
	public static final int BLOCK_SIZE = 1000;
	public static final int SAMPLING_RATE = 100;
	
	// FXML files roots
	public static final String LOG_IN_VIEW = "/patient/view/LogInLayout.fxml";
	public static final String REGISTRATION_VIEW = "/patient/view/RegistrationLayout.fxml";
	public static final String INSERT_ID_VIEW = "/patient/view/InsertIdLayout.fxml";
	public static final String DIALOG_POP_UP_VIEW = "/patient/view/DialogPopUpLayout.fxml";	
}
