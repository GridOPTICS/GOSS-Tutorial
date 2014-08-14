package pnnl.goss.tutorial.launchers;

import java.io.Serializable;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.DataResponse;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.core.client.GossClient.PROTOCOL;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.server.core.GossRequestHandlerRegistrationService;
import pnnl.goss.tutorial.PMUAggregator;
import pnnl.goss.tutorial.impl.PMUAggregatorImpl;


@Component
@Instantiate
public class AggregatorLauncher extends Thread{
	PMUAggregator aggregator; // = new PMUAggregatorImpl(client);
//	private static Client client = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"),PROTOCOL.STOMP);
	
	private volatile GossRequestHandlerRegistrationService registrationService;
	GossClient client = null; 

	private static Logger log = LoggerFactory
			.getLogger(AggregatorLauncher.class);
	
	private AggregatorLauncher launcher; 
	private boolean running = false;
	
//	public static void main(String[] args){
//		new AggregatorLauncher().startLauncher();
//		
//	}
	
	public AggregatorLauncher(@Requires GossRequestHandlerRegistrationService registrationService){
		try{
			log.debug("Creating aggregator Launcher "+registrationService);
			this.registrationService = registrationService;
			if(registrationService!=null && registrationService.getCoreServerConfig()!=null){
			
				client = new GossClient(PROTOCOL.STOMP);
				log.debug("CoreServerConfig "+this.registrationService.getCoreServerConfig());
				client.setConfiguration(this.registrationService.getCoreServerConfig());
				aggregator = new PMUAggregatorImpl(client);
			} else {
				log.error("Aggregator received null core server config");
			}
		}catch(Exception e){
			log.error("Failing while creating aggregator launcher", e);
		}
		
	}
	
	
	@Validate
	public void startLauncher(){
		log.debug("Starting Aggregator Launcher");
		if(launcher==null){
			launcher = new AggregatorLauncher(this.registrationService);
			launcher.start();
		}
	}
	
	@Invalidate
	public void stopLauncher(){
		log.debug("Stopping Aggregator Launcher");
		launcher.stop();
		launcher=null;
	}
	
	@Override
	public void run() {
		log.debug("Creating aggregator launcher");
		GossResponseEvent event = new GossResponseEvent() {
			public void onMessage(Serializable response) {
				String message = (String)((DataResponse)response).getData();
				log.debug("Aggregator got message "+message);
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
