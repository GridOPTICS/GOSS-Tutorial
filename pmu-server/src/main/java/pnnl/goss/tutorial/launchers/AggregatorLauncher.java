package pnnl.goss.tutorial.launchers;
import java.io.Serializable;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.http.auth.UsernamePasswordCredentials;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.core.client.GossClient.PROTOCOL;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.tutorial.PMUAggregator;
import pnnl.goss.tutorial.impl.PMUAggregatorImpl;


@Component
@Instantiate
public class AggregatorLauncher extends Thread{
	PMUAggregator aggregator = new PMUAggregatorImpl(client);
	private static Client client = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"),PROTOCOL.STOMP);
	private AggregatorLauncher launcher; 
	private boolean running = false;
	
	public static void main(String[] args){
		new AggregatorLauncher().startLauncher();
		
	}
	
	
	@Validate
	public void startLauncher(){
	
		if(launcher==null){
			launcher = new AggregatorLauncher();
			launcher.start();
		}
	}
	
	@Invalidate
	public void stopLauncher(){
		launcher.stop();
		launcher=null;
	}
	
	@Override
	public void run() {
		System.out.println("CREATE AGGREGATOR LAUNCHER");
		GossResponseEvent event = new GossResponseEvent() {
			public void onMessage(Serializable response) {
				String message = (String)((DataResponse)response).getData();
				System.out.println("AGG GOT MESSAGE "+message);
				if(message.contains("start agg") && running==false){
					launch();
					running=true;
				}
				if(message.contains("stop agg") && running==true){
					aggregator.stop();
					running=false;
				}
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
		aggregator.startCalculatePhaseAngleDifference(pmu1Topic, pmu2Topic, outputTopic);
	}
}
