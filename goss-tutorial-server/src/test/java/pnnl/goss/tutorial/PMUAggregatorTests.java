package pnnl.goss.tutorial;


import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.tutorial.impl.PMUAggregatorImpl;


public class PMUAggregatorTests {
	
	/*
	 * TODO Test for ordering to see if we care about magnitude or vector of difference.
	 */

	@Test
	public void testInputAndOutputTopicsSetProperly(){
		FakeClient fakeClient = new FakeClient();
		
		String topic1 = "/goss/topic1";
		String topic2 = "/goss/topic2";
		String outputTopic = "/goss/topic1_topic2_phase_angle_diff";
		
		PMUAggregator agg = new PMUAggregatorImpl(fakeClient);
		
		agg.startCalculatePhaseAngleDifference(topic1, topic2, outputTopic);
		Assert.assertEquals(topic1, agg.getPmu1Topic());
		Assert.assertEquals(topic2, agg.getPmu2Topic());
		Assert.assertEquals(outputTopic, agg.getOutputTopic());
		
	}
	
		
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
		
		// Make sure the Aggregator has subscribed to the passed topics.
		Assert.assertTrue(fakeClient.isSubscribed(topic1));
		Assert.assertTrue(fakeClient.isSubscribed(topic2));
		
		final HashSet<String> wasHit = new HashSet<String>();
		
		fakeClient.subscribeTo(outputTopic, new GossResponseEvent() {			
						
			@Override
			public void onMessage(Serializable response) {
				
				System.out.println(response);
				//String args[] = value.split(",");
				
				//Assert.assertEquals("2014-06-16 12:23:01", args[0]);
				//Assert.assertEquals("54.3", args[1]);
				
				wasHit.add(response.toString());
			}
		});
		
		
		fakeClient.publishString(topic1, "2014-06-16 12:23:01,59.3");
		fakeClient.publishString(topic2, "2014-06-16 12:23:01,5");
		Assert.assertEquals(1, wasHit.size());
		
		
		
				
		//listen to output topic on mock client 
		//mock compare expected result
		
	}
}
