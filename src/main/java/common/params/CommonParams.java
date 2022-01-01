package common.params;

public @interface CommonParams {

	// RestAPI connection URL
	public static final String BASE_URL = "http://localhost:8080/TelemedicineRestAPI";
	
	// Regex valid letters for DNI (ID)
	public static final String DNI_LETTERS = "[T,R,W,A,G,M,Y,F,P,X,B,N,J,Z,S,Q,V,H,L,C,K,E]";
	
	// Parameters for BITalino record and display data on graphs
	public static final int BLOCK_SIZE = 1000;
	public static final int SAMPLING_RATE = 100;
}
