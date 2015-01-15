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
import static pnnl.goss.tutorial.impl.PMUConstants.*;

public class AggregatorLauncher implements GossResponseEvent{
    private static Logger log = LoggerFactory.getLogger(AggregatorLauncher.class);

    private PMUAggregator aggregator; // = new PMUAggregatorImpl(client);
    private Client client = null;
    private volatile boolean running = false;
    private volatile boolean enabled = false;

    public AggregatorLauncher(Client client){
        if (client == null){
            throw new IllegalArgumentException("Client cannot be null!");
        }
        this.client = client;
        aggregator = new PMUAggregatorImpl(client);
    }

    public void startLauncher(){
        log.debug("Starting Aggregator Launcher");

        if (!enabled){
            enabled = true;
            log.debug("Enabling control channel");
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    setupControlChannel();
                    log.debug("Thread exiting: closing client now!");
                    // Close the client here.
                    client.close();
                }
            });

            thread.start();
        }
    }

    public void stopLauncher(){
        log.debug("Stopping Aggregator Launcher");
        stopAggregator();
        enabled = false;
        running = false;
        client.close();
    }


    private void startAggregator() {
        aggregator.startCalculatePhaseAngleDifference(PMU_1_TOPIC,
                PMU_2_TOPIC, PMU_AGGREGATION_TOPIC);
    }

    private void stopAggregator(){
        aggregator.stop();
    }

    private void setupControlChannel() {
        log.debug("Setting up Control Channel");

        client.subscribeTo("/topic/goss/tutorial/control", this);

    }


    @Override
    public synchronized void onMessage(Serializable response) {

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
