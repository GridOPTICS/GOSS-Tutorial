package pnnl.goss.tutorial.common;

import java.util.List;

import pnnl.goss.core.Client;

public interface PMUGenerator {

    boolean isRunning();
    String getPMUId();
    int getNumPublishedItemsPerInterval();
    double getIntervalSeconds();
    String getOutputTopic();
    void setClient(Client client);
    void setData(List<String> data);


    /**
     * Starts generating data and pushing data to the topic.
     *
     * @param pmuId
     * @param outputTopic
     * @param itemsPerInterval - Number of items published per interval.
     * @param intervalSeconds  - How many seconds between publishing items.
     */
    void start(String pmuId, String outputTopic, int itemsPerInterval, double intervalSeconds);

    /**
     * Stops generating data.
     */
    void stop();


}
