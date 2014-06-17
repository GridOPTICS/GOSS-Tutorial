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
				
				
		String[] data = {
			"30.5,20.5,80.5",
			"20.5,20.5,10.5",
			"40.5,9.5,7.5",
			"70.5,780.5,50.5",
			"80.5,40.5,50.5"
		};
		
		FakeResponseEvent evnt = new FakeResponseEvent();
		
		
		
		client.subscribeTo(pmuTopic, evnt);
		
		PMUGeneratorImpl gen = new PMUGeneratorImpl(client, Arrays.asList(data));
						
		gen.start(pmuId,  pmuTopic, 2, 5);
		
		long start = new Date().getTime();
		long offset = 0;
		while(evnt.getNumberItems() < data.length && offset < 1500){
			String item = evnt.getLastReturned();
			if (item != null){
				assertEquals(data[evnt.getNumberItems()], item);
			
				offset = new Date().getTime() - start;
			}
		}
		
		assertEquals(evnt.getNumberItems(), data.length);
	}
	
	class FakeResponseEvent implements GossResponseEvent{
		
		private ArrayList<String> items = new ArrayList<String>();
		private String lastItem;
		public String getLastReturned(){
			return lastItem;
		}
		public int getNumberItems(){
			return items.size();
		}
		
		public List<String> getItemsReturned(){
			return items;
		}

		@Override
		public void onMessage(Response response) {
			lastItem = (String)((DataResponse)response).getData();
			items.add(lastItem);			
		}
		
	}
	
}
