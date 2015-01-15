package pnnl.goss.tutorial.launchers;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.Client;
import pnnl.goss.core.DataResponse;
import pnnl.goss.core.GossResponseEvent;
import pnnl.goss.tutorial.common.PMUGenerator;
import pnnl.goss.tutorial.impl.PMUGeneratorImpl;
import static pnnl.goss.tutorial.impl.PMUConstants.*;

public class GeneratorLauncher implements GossResponseEvent {

    private static Logger log = LoggerFactory.getLogger(GeneratorLauncher.class);
    private volatile Client client;
    private volatile boolean running = false;
    private volatile boolean enabled = false;
    private PMUGenerator generator1;
    private PMUGenerator generator2;
    private Runnable runnable = null;

    public GeneratorLauncher(Client client) {
        log.debug("Constructing with client!");
        if (client == null) throw new IllegalArgumentException("Invalid client speecified!");
        this.client = client;
        this.generator1 = new PMUGeneratorImpl(client);
        this.generator2 = new PMUGeneratorImpl(client);
        createGenerators();
    }

    @Override
    public void onMessage(Serializable response) {
        String message = (String) ((DataResponse) response).getData();
        log.debug("Generator received message " + message);
        if (message.contains("start pmu") && running == false) {
            startGenerating();
            running = true;
        }
        if (message.contains("stop pmu") && running == true) {
            stopGenerating();
            running = false;
        }

        if (message.contains("show")) {
            log.debug("GEN1 " + generator1.getPMUId());
            log.debug("GEN2 " + generator2.getPMUId());
        }

    }

    private void setupControlChannel() {
        log.debug("Setting up Control Channel");

        client.subscribeTo("/topic/goss/tutorial/control", this);

    }

    public void stopLauncher(){
        if (enabled) {
            client.close();
        }
        generator1.stop();
        generator2.stop();
        running = false;
        enabled = false;

        log.debug("Stopping launcher");
    }


    public void startLauncher(){
        log.debug("Starting Generator Launcher");

        if (client == null){
            throw new NullPointerException("Specify client object");
        }

        if (!enabled){
            enabled = true;
            log.debug("Enabling control channel");
            runnable = new Runnable() {

                @Override
                public void run() {
                    setupControlChannel();
                    log.debug("Thread exiting: closing client now!");
                    // Close the client here.
                    client.close();
                }
            };

            new Thread(runnable).start();
        }
    }

    private void startGenerating() {
        log.debug("Starting generation");

        generator1.start(PMU_1_NAME, PMU_1_TOPIC, PUBLISH_PMU_PER_INTERVAL,
                PUBLISH_INTERVAL_SECONDS);
        generator2.start(PMU_2_NAME, PMU_2_TOPIC, PUBLISH_PMU_PER_INTERVAL,
                PUBLISH_INTERVAL_SECONDS);
    }

    private void stopGenerating() {
        log.debug("Stopping Generation");
        generator1.stop();
        generator2.stop();
    }


    protected void createGenerators() {

        try {
            log.debug("Creating Generators");
            Random random = new Random();
            double max = 1.0;
            double min = 0.0;
            Date datetime = null;
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String phase;
            String freq;
            int totalValues = 1000;
            String dataArr1[] = new String[totalValues];
            String dataArr2[] = new String[totalValues];

            // /Create timestamp
            for (int i = 0; i < totalValues; i++) {

                if (datetime == null) {
                    datetime = formatter.parse("2014-07-10 01:00:00.000");
                } else {
                    long milliseconds = 33;
                    datetime.setTime(datetime.getTime() + milliseconds);
                }

                // Create data for PMU 1 Phasor 1 stream
                min = -15;
                max = 15;
                phase = decimalFormat.format(min + (max - min)
                        * random.nextDouble());
                min = 58;
                max = 62;
                freq = decimalFormat.format(min + (max - min)
                        * random.nextDouble());
                dataArr1[i] = formatter.format(datetime) + "," + phase + ","
                        + freq;

                // Create data for PMU 2 Phasor 1 stream
                min = -15;
                max = 15;
                phase = decimalFormat.format(min + (max - min)
                        * random.nextDouble());
                min = 58;
                max = 62;
                freq = decimalFormat.format(min + (max - min)
                        * random.nextDouble());
                dataArr2[i] = formatter.format(datetime) + "," + phase + ","
                        + freq;

            }

            generator1.setData(Arrays.asList(dataArr1));
            generator2.setData(Arrays.asList(dataArr2));

        } catch (ParseException pe) {
            log.error("PMU stream date is not in the correct format", pe);
            pe.printStackTrace();
        }
    }
}
