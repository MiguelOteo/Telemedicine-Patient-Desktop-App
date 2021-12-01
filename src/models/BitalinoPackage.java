package models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class BitalinoPackage {

	private int bitalinoPackageId;
	
	private int patientId;
	private int recordFreq;
	private Date recordsDate;
	private String emgData;
	private String ecgData;

	public BitalinoPackage(int patientId, int recordFreq, Date now, String emgData, String ecgData) {
		
		this.patientId = patientId;
		this.recordFreq = recordFreq;
		this.recordsDate = now;
		this.emgData = emgData;
		this.ecgData = ecgData;
		
	}
	
	public BitalinoPackage(int bitalinoPackageId, int patientId, int recordFreq, Date startingDate, String emgData, String ecgData) {
		
		this.bitalinoPackageId = bitalinoPackageId;
		
		this.patientId = patientId;
		this.recordFreq = recordFreq;
		this.recordsDate = startingDate;
		this.emgData = emgData;
		this.ecgData = ecgData;
	}

	public int getBitalinoPackageId() {return bitalinoPackageId;}

	public void setBitalinoPackageId(int bitalinoPackageId) {this.bitalinoPackageId = bitalinoPackageId;}

	public int getPatientId() {return patientId;}

	public void setPatientId(int patientId) {this.patientId = patientId;}

	public int getRecordFreq() {return recordFreq;}

	public void setRecordFreq(int recordFreq) {this.recordFreq = recordFreq;}

	public Date getRecordsDate() {return recordsDate;}

	public void setRecordsDate(Date recordsDate) {this.recordsDate = recordsDate;}

	public String getemgData() {return emgData;}

	public void setemgData(String emgData) {this.emgData = emgData;}
	
	public String getecgData() {return ecgData;}

	public void setecgData(String ecgData) {this.ecgData = ecgData;}
}

