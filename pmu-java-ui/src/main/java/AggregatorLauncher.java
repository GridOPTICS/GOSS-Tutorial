import org.apache.http.auth.UsernamePasswordCredentials;

import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.tutorial.PMUAggregator;
import pnnl.goss.tutorial.impl.PMUAggregatorImpl;



public class AggregatorLauncher {
	PMUAggregator aggregator;
	
	public static void main(String[] args){
		new AggregatorLauncher().launch();
	}
	
	protected void launch(){
		Client client = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"));
		String pmu1Id = "PMU_1";
		String pmu2Id = "PMU_2";
		String pmu1Topic = "/pmu/"+pmu1Id;
		String pmu2Topic = "/pmu/"+pmu2Id;
		String outputTopic = "/pmu/"+pmu1Id+"/"+pmu2Id+"/padiff";
		aggregator = new PMUAggregatorImpl(client);
		aggregator.startCalculatePhaseAngleDifference(pmu1Topic, pmu2Topic, outputTopic);
		
	}
}
