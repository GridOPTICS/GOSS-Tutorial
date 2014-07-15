import java.util.Date;

import pnnl.goss.core.DataError;
import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.request.TutorialDownloadRequestAsync;
import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;


public class DownloadLauncher {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		asynchronousTest();
	}

	
	
	static void asynchronousTest(){
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try{
						final GossClient client = new GossClient();
						Date startDate = PMUPhaseAngleDiffData.DATE_FORMAT.parse(("2014-07-01 00:00:00.000"));
						TutorialDownloadRequestAsync request = new TutorialDownloadRequestAsync(1, 1270105200, 10000, startDate);
						
						GossResponseEvent event = new GossResponseEvent() {
							public void onMessage(Response response) {
								DataResponse dataresponse=null;
								try{
									dataresponse = (DataResponse)response;
									Object data  = (dataresponse).getData();
									System.out.println(response.sizeof());
									if(dataresponse.isResponseComplete()){
										client.close();
									}
								}
								catch(ClassCastException cce){
									if(dataresponse!=null){
										DataError error = (DataError)dataresponse.getData();
										System.out.println(error.getMessage());
									}
									else 
										throw cce;
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
						};
						client.sendRequest(request, event, null);
						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			thread.start();
	}
}
