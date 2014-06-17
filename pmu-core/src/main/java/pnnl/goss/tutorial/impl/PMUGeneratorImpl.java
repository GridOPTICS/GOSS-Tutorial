package pnnl.goss.tutorial.impl;

import java.io.File;
import java.util.List;
import java.util.Stack;

import org.apache.felix.ipojo.annotations.Property;

import pnnl.goss.core.client.Client;
import pnnl.goss.tutorial.PMUGenerator;

public class PMUGeneratorImpl implements PMUGenerator{
	
	private final Client client;
	private Stack<String> data;
	private String outputTopic;
	
	private boolean isRunning = false;
	private String pmuId;
	private int itemsPerInterval;
	private double intervalSeconds;
		
	public PMUGeneratorImpl(@Property Client client, List<String> data){
		this.client = client;
		this.data = new Stack<String>();
		this.data.addAll(data);
	}
	
	private void publishNext(){
		String item = data.pop();		
		this.client.publish(outputTopic, item);		
	}
	
//	public PMUGeneratorImpl(@Property Client client, boolean autoGen){
//		this.client = client;
//		
//	}
//	
//	public PMUGeneratorImpl(@Property Client client, File file){
//		this.client = client;
//	}

	@Override
	public void start(String pmuId, String outputTopic, final int itemsPerIterval, final double intervalSeconds) {
		this.isRunning = true;
		this.pmuId = pmuId;
		this.itemsPerInterval = itemsPerIterval;
		this.intervalSeconds = intervalSeconds;
		this.outputTopic = outputTopic;
				
		Thread thread = new Thread(){
			public void run(){
				
				while(isRunning){
					for(int i=0;i<itemsPerIterval && !data.empty(); i++){
						publishNext();
					}
					
					try {
						Thread.sleep((int)(intervalSeconds*1000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		
		thread.start();
		
	}

	@Override
	public void stop() {
		isRunning = false;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public String getPMUId() {
		return pmuId;
	}

	@Override
	public int getNumPublishedItemsPerInterval() {
		return itemsPerInterval;
	}

	@Override
	public String getOutputTopic() {
		return outputTopic;
	}

	@Override
	public double getIntervalSeconds() {
		return intervalSeconds;
	}

	
}
