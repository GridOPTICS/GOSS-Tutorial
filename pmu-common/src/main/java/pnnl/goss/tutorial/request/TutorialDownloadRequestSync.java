package pnnl.goss.tutorial.request;

import java.util.Date;

import pnnl.goss.core.Request;

public class TutorialDownloadRequestSync extends Request {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 725244558288729622L;
	Date startDate;
	
	public TutorialDownloadRequestSync(Date startDate){
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	

}
