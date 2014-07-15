package pnnl.goss.tutorial.launchers;
import org.apache.http.auth.UsernamePasswordCredentials;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.core.client.GossClient.PROTOCOL;
import pnnl.goss.tutorial.PMUAggregator;
import pnnl.goss.tutorial.impl.PMUAggregatorImpl;



public class AggregatorLauncher extends Thread{
	PMUAggregator aggregator;
	private static Client client = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"),PROTOCOL.STOMP);
	
	public static void main(String[] args){
		
		final AggregatorLauncher launcher = new AggregatorLauncher();
		launcher.start();
		
	}
	@Override
	public void run() {
		System.out.println("CREATE AGGREGATOR LAUNCHER");
		GossResponseEvent event = new GossResponseEvent() {
			public void onMessage(Response response) {
				String message = (String)((DataResponse)response).getData();
				System.out.println("AGG GOT MESSAGE "+message);
				if(message.contains("start agg"))
					launch();
			}
		};
		client.subscribeTo("/topic/goss/tutorial/control", event);
	}
	
	protected void launch(){
		String pmu1Id = "PMU_1";
		String pmu2Id = "PMU_2";
		String pmu1Topic = "/topic/goss/tutorial/pmu/PMU_1";
		String pmu2Topic = "/topic/goss/tutorial/pmu/PMU_2";
		String outputTopic = "pmu/"+pmu1Id+"/"+pmu2Id+"/agg";
		aggregator = new PMUAggregatorImpl(client);
		aggregator.startCalculatePhaseAngleDifference(pmu1Topic, pmu2Topic, outputTopic);
		
	}
}
