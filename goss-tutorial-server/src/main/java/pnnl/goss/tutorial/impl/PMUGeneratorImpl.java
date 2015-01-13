package pnnl.goss.tutorial.impl;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.Client;
import pnnl.goss.tutorial.common.PMUGenerator;

public class PMUGeneratorImpl implements PMUGenerator{
    private static Logger log = LoggerFactory.getLogger(PMUGeneratorImpl.class);

    private List<String> data;
    private Queue<String> queue;
    private String outputTopic;
    private Client client;

    private boolean isRunning = false;
    private String pmuId;
    private int itemsPerInterval;
    private double intervalSeconds;

    public void setClient(Client client){
        this.client = client;
    }

    public PMUGeneratorImpl(Client client){

        if (client == null) {
            log.debug("Null client passed!");
            throw new IllegalArgumentException("Invalid client speecified!");
        }
        this.client = client;
        log.debug("Client is: "+client.toString());
    }

    public PMUGeneratorImpl(Client client, List<String> data){
        this.client = client;
        this.data = data;
    }

    private synchronized void publishNext(){
        String item = queue.poll();

        log.debug("publishing next: "+outputTopic+" " + item);
        this.client.publishString(outputTopic, item);
    }

    private void createQueue(){
        this.queue = new LinkedBlockingDeque<String>();
        this.queue.addAll(data);
    }

    @Override
    public void start(String pmuId, String outputTopic, final int itemsPerIterval, final double intervalSeconds) {
        if (client == null){
            throw new IllegalStateException("Invalid client reference!");
        }
        if (data == null){
            throw new IllegalStateException("Invalid data specified");
        }

        log.debug("Starting generation for pmu: "+pmuId);
        log.debug("Output topic: "+outputTopic);
        this.isRunning = true;
        this.pmuId = pmuId;
        this.itemsPerInterval = itemsPerIterval;
        this.intervalSeconds = intervalSeconds;
        this.outputTopic = outputTopic;
        createQueue();

        Thread thread = new Thread(){
            public void run(){

                while(isRunning){
                    for(int i=0;i<itemsPerIterval && !queue.isEmpty(); i++){
                        publishNext();
                    }

                    try {
                        Thread.sleep((int)(intervalSeconds*1000));
                    } catch (InterruptedException e) {
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

    @Override
    public void setData(List<String> data) {
        this.data = data;
    }
}
