package pnnl.goss.tutorial;



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
	
	
}
