package pnnl.goss.tutorial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.GossResponseEvent;

class FakeResponseEvent implements GossResponseEvent{
	private ArrayList<Long> arrivalTimes = new ArrayList<Long>();
	private ArrayList<String> items = new ArrayList<String>();
	private String lastItem;
	public String getLastReturned(){
		return lastItem;
	}
	public int getNumberItems(){
		return items.size();
	}
	
	public List<String> getItemsReturned(){
		return items;
	}
	
	public List<Long> getArrivalTimes(){
		return arrivalTimes;
	}

	@Override
	public void onMessage(Response response) {
		lastItem = (String)((DataResponse)response).getData();
		items.add(lastItem);			
		arrivalTimes.add(new Date().getTime());
	}
	
}
