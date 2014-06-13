package pnnl.goss.tutorial.impl;


import org.apache.felix.ipojo.annotations.Property;

import pnnl.goss.tutorial.PMUAggregator;
import pnnl.goss.core.client.GossClient;

public class PMUAggregatorImpl implements PMUAggregator{

	
	final GossClient client;
	
	
	public PMUAggregatorImpl(@Property GossClient client){
		this.client = client;
	}
	
	public void startCalculatePhaseAngleDifference(String topic1, String topic2, String outputTopic) {
		// TODO Auto-generated method stub
		
	}
	
	

}
