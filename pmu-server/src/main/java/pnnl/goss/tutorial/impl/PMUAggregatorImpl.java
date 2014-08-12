package pnnl.goss.tutorial.impl;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.felix.ipojo.annotations.Property;
import org.apache.http.auth.UsernamePasswordCredentials;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.core.client.GossClient.PROTOCOL;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.tutorial.PMUAggregator;
import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PMUAggregatorImpl implements PMUAggregator{

	final Client client;
	
	private String pmu1Topic;
	private String pmu2Topic;
	private String outputTopic;
	private static Client client1 = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"),PROTOCOL.STOMP);
	private static Client client2 = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"),PROTOCOL.STOMP);
	private volatile boolean isRunning = true;
	
	public PMUAggregatorImpl(@Property Client client){
		this.client = client;
	}
	
	private void publishDifference(Date date, Double value1, Double value2){
		Double value = value1-value2;
		
		PMUPhaseAngleDiffData data = new PMUPhaseAngleDiffData();
		data.setPhasor1(value1);
		data.setPhasor2(value2);
		data.setDifference(value);
		data.setTimestamp(date);
		Gson gson = new GsonBuilder().setDateFormat(PMUPhaseAngleDiffData.DATE_FORMAT.toPattern()).create();
		
		String json = gson.toJson(data);
		System.out.println("Publishing "+json+" to "+outputTopic);
		client.publish(outputTopic, json);

	}
	
	public void startCalculatePhaseAngleDifference(String topic1, String topic2, String outputTopic) {
		isRunning = true;
		pmu1Topic = topic1;
		pmu2Topic = topic2;
		this.outputTopic = outputTopic;
		final HashMap<Date, Double> topic1Values = new HashMap<Date, Double>();
		final HashMap<Date, Double> topic2Values = new HashMap<Date, Double>();
		
		
		Thread thread1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				GossResponseEvent event1  = new GossResponseEvent() {
					
					@Override
					public void onMessage(Serializable response) {
						if(isRunning){
							String responseStr = ((DataResponse)response).getData().toString();
							String args[] = responseStr.split(",");
							Date date = null;
							Double dblValue = null;
							try {
								SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								date = DATE_FORMAT.parse(args[0].trim());
								dblValue = Double.parseDouble(args[1].trim());
								topic1Values.put(date, dblValue);
							} catch (ParseException e) {
								System.err.println("Could not parse date "+args[0]);
								e.printStackTrace();
							} catch (Exception e) {
								System.err.println("Exception in pmu 1 stream");
								e.printStackTrace();
							}
							if (date != null && topic2Values.containsKey(date)){
								publishDifference(date, dblValue, topic2Values.get(date));
								topic1Values.remove(date);
								topic2Values.remove(date);
							}
						}
					}
				};
				client1.subscribeTo(pmu1Topic, event1);
				
			}
		});
		
		Thread thread2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				GossResponseEvent event2 = new GossResponseEvent() {
					
					@Override
					public void onMessage(Serializable response) {
						if(isRunning){
						String responseStr = ((DataResponse)response).getData().toString();
						String args[] = responseStr.split(",");
						Date date = null;
						Double dblValue = null;
						try {
							SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							date = DATE_FORMAT.parse(args[0].trim());
							dblValue = Double.parseDouble(args[1].trim());	
							topic2Values.put(date, dblValue);
						} catch (ParseException e) {
							System.err.println("Could not parse date "+args[0].trim());
							e.printStackTrace();
						} catch (Exception e) {
							System.err.println("Exception in pmu 2 stream");
							e.printStackTrace();
						}
						
						if (date != null && topic1Values.containsKey(date)){
							publishDifference(date, topic1Values.get(date), dblValue);
							topic1Values.remove(date);
							topic2Values.remove(date);
						}
						}
					}
				};
				
				client2.subscribeTo(pmu2Topic, event2);
				
			}
		});
		
		thread1.start();
		thread2.start();
		
	}
	
	public void stop(){
		isRunning = false;
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
