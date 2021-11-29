package models;

import java.sql.Date;

public class BitalinoPackage {

	private int bitalinoPackageId;
	
	private int patientId;
	private int recordFreq;
	private Date recordsDate;
	private String recordsData;

	public BitalinoPackage(int patientId, int recordFreq, Date now, String recordsData) {
		
		this.patientId = patientId;
		this.recordFreq = recordFreq;
		this.recordsDate = now;
		this.recordsData = recordsData;
		
	}
	
	public BitalinoPackage(int bitalinoPackageId, int patientId, int recordFreq, Date startingDate, String recordsData) {
		
		this.bitalinoPackageId = bitalinoPackageId;
		
		this.patientId = patientId;
		this.recordFreq = recordFreq;
		this.recordsDate = startingDate;
		this.recordsData = recordsData;
	}

	public int getBitalinoPackageId() {return bitalinoPackageId;}

	public void setBitalinoPackageId(int bitalinoPackageId) {this.bitalinoPackageId = bitalinoPackageId;}

	public int getPatientId() {return patientId;}

	public void setPatientId(int patientId) {this.patientId = patientId;}

	public int getRecordFreq() {return recordFreq;}

	public void setRecordFreq(int recordFreq) {this.recordFreq = recordFreq;}

	public Date getRecordsDate() {return recordsDate;}

	public void setRecordsDate(Date recordsDate) {this.recordsDate = recordsDate;}

	public String getRecordsData() {return recordsData;}

	public void setRecordsData(String recordsData) {this.recordsData = recordsData;}
}

