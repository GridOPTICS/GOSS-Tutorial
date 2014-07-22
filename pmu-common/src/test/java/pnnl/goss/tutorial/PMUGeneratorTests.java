package pnnl.goss.tutorial;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.tutorial.impl.PMUGeneratorImpl;

public class PMUGeneratorTests {
	
	@Test
	public void testStartAndStopState() {
		FakeClient client = new FakeClient();
		String pmuId = "PMU_1";
		String pmuTopic = "/pmu/"+pmuId;
		
		PMUGeneratorImpl gen = new PMUGeneratorImpl(client, new ArrayList<String>());
		
		gen.start(pmuId,  pmuTopic, 50, 5);
		assertEquals(pmuId, gen.getPMUId());
		assertTrue(gen.isRunning());
		assertEquals(50, gen.getNumPublishedItemsPerInterval());
		assertEquals(pmuTopic, gen.getOutputTopic());
		assertEquals(5, gen.getIntervalSeconds(), 0.05);
		gen.stop();
		assertFalse(gen.isRunning());
	}
	
	@Test
	public void testTopicDataSent(){
		FakeClient client = new FakeClient();
		String pmuId = "PMU_1";
		String pmuTopic = "/pmu/"+pmuId;
		int itemsPerInterval = 2;
		double intervalSeconds = 1;
								
		String[] data = {
			//"phase, angle, freq"
			"2014-07-10 01:00:00.000,30.5,20.5,80.5",
			"2014-07-10 01:00:00.033,20.5,20.5,10.5",
			"2014-07-10 01:00:00.066,40.5,9.5,7.5",
			"2014-07-10 01:00:00.099,70.5,780.5,50.5",
			"2014-07-10 01:00:00.132,80.5,40.5,50.5"
		};
		
		FakeResponseEvent evnt = new FakeResponseEvent();		
		
		
		client.subscribeTo(pmuTopic, evnt);
		
		assertTrue(client.isSubscribed(pmuTopic));
		
		PMUGeneratorImpl gen = new PMUGeneratorImpl(client, Arrays.asList(data));
						
		gen.start(pmuId,  pmuTopic, itemsPerInterval, intervalSeconds);
		
		long start = new Date().getTime();
		long offset = 0;

		while(evnt.getNumberItems() < data.length && offset < data.length*intervalSeconds*1000){
			offset = new Date().getTime() - start;
		}
		
//		System.out.println("GOT ALL "+evnt.getNumberItems());
		
		assertEquals(evnt.getNumberItems(), data.length);
		
		List<Long> arrivals = evnt.getArrivalTimes();
		for(int i=0;i<data.length; i++){
			if (i < data.length - 1){
				assertTrue(arrivals.get(i)<= arrivals.get(i+1));
			}
			
			if (i < data.length - itemsPerInterval){
				assertTrue((arrivals.get(i+itemsPerInterval)- arrivals.get(i)) >= intervalSeconds*1000);
			}
			
			assertEquals(data[i], evnt.getItemsReturned().get(i));			
		}
		//TODO check total time
		//long totalRun = arrivals.get(arrivals.size()-1) - arrivals.get(0);
		//eh
		
	}	
}
