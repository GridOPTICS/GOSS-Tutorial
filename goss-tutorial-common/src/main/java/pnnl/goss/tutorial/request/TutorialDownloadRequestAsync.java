package pnnl.goss.tutorial.request;

import java.util.Date;

import pnnl.goss.core.RequestAsync;

public class TutorialDownloadRequestAsync extends RequestAsync {
	
	Date startDate;
	long startTime;
	long endTime;
	int count;
	int segment;
	
	public TutorialDownloadRequestAsync(long startTime, long endTime, int segment, Date startDate){
		this.startTime = startTime;
		this.endTime = endTime;
		this.segment = segment;
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSegment() {
		return segment;
	}

	public void setSegment(int segment) {
		this.segment = segment;
	}
	
	
	

}
