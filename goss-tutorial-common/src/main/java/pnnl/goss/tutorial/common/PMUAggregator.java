package pnnl.goss.tutorial.common;



public interface PMUAggregator {

    /**
     * starts the calculation of phase angle difference between PMU data coming from two topics
     * TODO  Figure out the expected PMU data format, probably something like (datetime, sensor?, #, #, #)
     *
     * @param topic1
     * @param topic2
     * @param outputTopic
     */
    public void startCalculatePhaseAngleDifference(String topic1, String topic2, String outputTopic);

    /**
     * Retrieve the topic where pmu1 is being published.
     * @return
     */
    public String getPmu1Topic();

    /**
     * Sets pmu1's topic
     * @param pmu1Topic
     */
    public void setPmu1Topic(String pmu1Topic);

    /**
     * Returns pmu2's topic
     * @return
     */
    public String getPmu2Topic();

    /**
     * Sets pmu2's topic
     * @param pmu2Topic
     */
    public void setPmu2Topic(String pmu2Topic);

    /**
     * The topic where the aggregator should store the difference
     * @return
     */
    public String getOutputTopic();

    /**
     * Sets the aggregator output topic.
     * @param outputTopic
     */
    public void setOutputTopic(String outputTopic);

    /**
     * Stop calculating the aggregation.
     */
    public void stop();

}
