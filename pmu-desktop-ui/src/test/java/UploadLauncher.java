

import java.util.Date;

import org.apache.http.auth.UsernamePasswordCredentials;

import pnnl.goss.core.UploadRequest;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;


public class UploadLauncher {
	
	private static Client client = new GossClient(new UsernamePasswordCredentials("pmu_user", "password"));
	
	public static void main(String[] args){
		
		try{
		
			PMUPhaseAngleDiffData data = new PMUPhaseAngleDiffData();
			data.setDifference(5.0);
			data.setPhasor1(5.0);
			data.setPhasor2(10.0);
			data.setTimestamp(new Date());
			
			UploadRequest request = new UploadRequest(data,"Tutorial");
			client.getResponse(request);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
