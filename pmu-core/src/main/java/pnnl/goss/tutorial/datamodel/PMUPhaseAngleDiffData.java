package pnnl.goss.tutorial.datamodel;

import java.io.Serializable;
import java.util.Date;

public class PMUPhaseAngleDiffData implements Serializable {
	
	private static final long serialVersionUID = 3219362975964656381L;
	private Date timestamp;
	private Double phasor1;
	private Double phasor2;
	private Double difference;
	
	
	
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Double getPhasor1() {
		return phasor1;
	}
	public void setPhasor1(Double phasor1) {
		this.phasor1 = phasor1;
	}
	public Double getPhasor2() {
		return phasor2;
	}
	public void setPhasor2(Double phasor2) {
		this.phasor2 = phasor2;
	}
	public Double getDifference() {
		return difference;
	}
	public void setDifference(Double difference) {
		this.difference = difference;
	}
	
	
	
}
