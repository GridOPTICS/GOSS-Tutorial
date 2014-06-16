package pnnl.goss.tutorial;

import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.tutorial.impl.PMUAggregatorImpl;


public class PMUAggregatorTests {

		
	@Test
	public void testCalculate(){
		//inject the mock client so we know how to connect to the topics
		//TODO add interface to goss client
		//call start
		FakeClient fakeClient = new FakeClient();
		GossResponseEvent mockedEvent = mock(GossResponseEvent.class);
		String topic1 = "/goss/topic1";
		String topic2 = "/goss/topic2";
		String outputTopic = "/goss/topic1_topic2_phase_angle_diff";
		
		PMUAggregator agg = new PMUAggregatorImpl(fakeClient);
		
		agg.startCalculatePhaseAngleDifference(topic1, topic2, outputTopic);
		
		assertTrue(fakeClient.isSubscribed(topic1));
		assertTrue(fakeClient.isSubscribed(topic2));
				
		//listen to output topic on mock client 
		//mock compare expected result
		
	}
}
