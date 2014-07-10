import java.util.Arrays;

import org.apache.http.auth.UsernamePasswordCredentials;

import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.tutorial.PMUGenerator;
import pnnl.goss.tutorial.impl.PMUGeneratorImpl;


public class GeneratorLauncher {
	private PMUGenerator generator1;
	private PMUGenerator generator2;
	
	
	public static void main(String[] args){
		new GeneratorLauncher().launch();	
	}
	
	protected void launch(){
		Client client = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"));
		String pmu1Id = "PMU_1";
		String pmu2Id = "PMU_2";
		String pmu1Topic = "/pmu/"+pmu1Id;
		String pmu2Topic = "/pmu/"+pmu2Id;
		int itemsPerInterval = 2;
		double intervalSeconds = 1;
		
		String[] dataArr1 = {
				//"phase, angle, freq"
				"30.5,20.5,80.5",
				"20.5,20.5,10.5",
				"40.5,9.5,7.5",
				"70.5,780.5,50.5",
				"80.5,40.5,50.5"
			};
		
		
		generator1 = new PMUGeneratorImpl(client,  Arrays.asList(dataArr1));
		generator1.start(pmu1Id, pmu1Topic, itemsPerInterval, intervalSeconds);
		
		
		String[] dataArr2 = {
				//"phase, angle, freq"
				"30.5,21.5,80.5",
				"20.5,19.8,10.5",
				"40.5,9.7,7.5",
				"70.5,780.0,50.5",
				"80.5,41.2,50.5"
			};
		
		generator2 = new PMUGeneratorImpl(client,  Arrays.asList(dataArr2));
		generator2.start(pmu2Id, pmu2Topic, itemsPerInterval, intervalSeconds);	
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		if(generator1!=null){
			generator1.stop();
		}
		
		if(generator2!=null){
			generator2.stop();
		}
	}
}
