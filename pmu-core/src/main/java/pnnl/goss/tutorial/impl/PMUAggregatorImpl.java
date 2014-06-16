package pnnl.goss.tutorial.impl;


import org.apache.felix.ipojo.annotations.Property;

import pnnl.goss.tutorial.PMUAggregator;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossResponseEvent;

public class PMUAggregatorImpl implements PMUAggregator{

	
	final Client client;
	private String pmu1Topic;
	private String pmu2Topic;
	private String outputTopic;
	
	
	public PMUAggregatorImpl(@Property Client client){
		this.client = client;
	}
	
	public void startCalculatePhaseAngleDifference(String topic1, String topic2, String outputTopic) {
		client.subscribeTo(topic1, new GossResponseEvent() {
			
			@Override
			public void onMessage(Response response) {
				// TODO Auto-generated method stub
				
			}
		});
		
		client.subscribeTo(topic2, new GossResponseEvent() {
			
			@Override
			public void onMessage(Response response) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		// Subscribe to client.topic1, and topic2.
		
	}

	public String getPmu1Topic() {
		return pmu1Topic;
	}

	public void setPmu1Topic(String pmu1Topic) {
		this.pmu1Topic = pmu1Topic;
	}

	public String getPmu2Topic() {
		return pmu2Topic;
	}

	public void setPmu2Topic(String pmu2Topic) {
		this.pmu2Topic = pmu2Topic;
	}

	public String getOutputTopic() {
		return outputTopic;
	}

	public void setOutputTopic(String outputTopic) {
		this.outputTopic = outputTopic;
	}
	
	

}
