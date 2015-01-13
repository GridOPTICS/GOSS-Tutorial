package pnnl.goss.tutorial.launchers;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.GossResponseEvent;
import pnnl.goss.core.Client;
import pnnl.goss.core.Client.PROTOCOL;
import pnnl.goss.core.server.GossRequestHandlerRegistrationService;
import pnnl.goss.tutorial.common.PMUAggregator;
import pnnl.goss.tutorial.impl.PMUAggregatorImpl;

public class AggregatorLauncher extends Thread implements GossResponseEvent{
    private static Logger log = LoggerFactory.getLogger(AggregatorLauncher.class);

    private PMUAggregator aggregator; // = new PMUAggregatorImpl(client);
    private Client client = null;
    private boolean running = false;

    public AggregatorLauncher(Client client){
        if (client == null){
            throw new IllegalArgumentException("Client cannot be null!");
        }
        this.client = client;
        this.client.setProtocol(PROTOCOL.STOMP);

    }


    public void startLauncher(){
        log.debug("Starting Aggregator Launcher");
        new Thread(this).start();
    }

    public void stopLauncher(){
        log.debug("Stopping Aggregator Launcher");
        stopAggregator();
    }

    @Override
    public void run() {
        log.debug("Creating aggregator launcher");
        setupControlChannel();
        startAggregator();
    }


    private void startAggregator() {
        String pmu1Id = "PMU_1";
        String pmu2Id = "PMU_2";
        String pmu1Topic = "/topic/goss/tutorial/pmu/PMU_1";
        String pmu2Topic = "/topic/goss/tutorial/pmu/PMU_2";
        String outputTopic = "pmu/"+pmu1Id+"/"+pmu2Id+"/agg";
        aggregator.startCalculatePhaseAngleDifference(pmu1Topic, pmu2Topic, outputTopic);
    }

    private void stopAggregator(){
        aggregator.stop();
    }

    private void setupControlChannel() {
        log.debug("Setting up Control Channel");

        client.subscribeTo("/topic/goss/tutorial/control", this);

    }


    @Override
    public void onMessage(Serializable response) {
        String message = (String)((DataResponse)response).getData();
        log.debug("Aggregator got message "+message);
        if(message.contains("start agg") && running==false){
            startAggregator();
            running=true;
        }
        if(message.contains("stop agg") && running==true){
            stopAggregator();
            running=false;
        }
    }
}
