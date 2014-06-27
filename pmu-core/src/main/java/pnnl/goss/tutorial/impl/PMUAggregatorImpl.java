package pnnl.goss.tutorial.impl;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;

import org.apache.felix.ipojo.annotations.Property;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Request.RESPONSE_FORMAT;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.tutorial.PMUAggregator;

public class PMUAggregatorImpl implements PMUAggregator{

	
	final Client client;
	final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String pmu1Topic;
	private String pmu2Topic;
	private String outputTopic;
	
	
	
	public PMUAggregatorImpl(@Property Client client){
		this.client = client;
	}
	
	private void publishDifference(Date date, Double value1, Double value2){
		String timestamp = fmt.format(date);
		Double value = value1-value2;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(pmu1Topic, value1.toString());
		map.put(pmu2Topic, value2.toString());
		map.put(outputTopic, value.toString());
		map.put("timestamp", timestamp);
		
		client.publish(outputTopic, map, RESPONSE_FORMAT.JSON);
	}
	
	public void startCalculatePhaseAngleDifference(String topic1, String topic2, String outputTopic) {
		pmu1Topic = topic1;
		pmu2Topic = topic2;
		this.outputTopic = outputTopic;
		final HashMap<Date, Double> topic1Values = new HashMap<Date, Double>();
		final HashMap<Date, Double> topic2Values = new HashMap<Date, Double>();
		client.subscribeTo(topic1, new GossResponseEvent() {
			
			@Override
			public void onMessage(Serializable response) {
				
				System.out.println(response);
				String args[] = response.toString().split(",");
				Date date = null;
				Double dblValue = null;
				try {
					date = fmt.parse(args[0]);
					dblValue = Double.parseDouble(args[1]);					
					topic1Values.put(date, dblValue);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (date != null && topic2Values.containsKey(date)){
					publishDifference(date, dblValue, topic2Values.get(date));
					topic1Values.remove(date);
					topic2Values.remove(date);
				}
			}
		});
		
		client.subscribeTo(topic2, new GossResponseEvent() {
			
			@Override
			public void onMessage(Serializable response) {
				String args[] = response.toString().split(",");
				Date date = null;
				Double dblValue = null;
				try {
					date = fmt.parse(args[0]);
					dblValue = Double.parseDouble(args[1]);					
					topic2Values.put(date, dblValue);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (date != null && topic1Values.containsKey(date)){
					publishDifference(date, topic1Values.get(date), dblValue);
					topic1Values.remove(date);
					topic2Values.remove(date);
				}
				
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
