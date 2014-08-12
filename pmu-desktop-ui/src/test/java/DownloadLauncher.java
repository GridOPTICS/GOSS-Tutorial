import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;

import pnnl.goss.core.Data;
import pnnl.goss.core.DataError;
import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.core.client.GossResponseEvent;
import pnnl.goss.tutorial.request.TutorialDownloadRequestAsync;
import pnnl.goss.tutorial.request.TutorialDownloadRequestSync;
import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;


public class DownloadLauncher {

	public static void main(String[] args) {
		//asynchronousTest();
		synchronousTest();
	}

	static void synchronousTest(){
		final GossClient client = new GossClient();
		Date startDate;
		try {
			startDate = PMUPhaseAngleDiffData.DATE_FORMAT.parse(("2014-07-01 00:00:00.000"));
		
			TutorialDownloadRequestSync request = new TutorialDownloadRequestSync(startDate);
			DataResponse response = (DataResponse)client.getResponse(request);
			Object data = response.getData();
			if(data instanceof DataError){
				System.err.println(((DataError)data).getMessage());
			} else {
				System.out.println(response.getData());
			}
			
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void asynchronousTest(){
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try{
						final GossClient client = new GossClient();
						Date startDate = PMUPhaseAngleDiffData.DATE_FORMAT.parse(("2014-07-01 00:00:00.000"));
						TutorialDownloadRequestAsync request = new TutorialDownloadRequestAsync(1, 1270105200, 1000, startDate);
						request.setFrequency(1000);
						GossResponseEvent event = new GossResponseEvent() {
							public void onMessage(Serializable response) {
								DataResponse dataresponse=null;
								try{
									dataresponse = (DataResponse)response;
									Object data  = (dataresponse).getData();
									System.out.println(data);
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
						String destination = client.sendRequest(request, event, null);
						System.out.println("DESTINATION IS "+destination);
						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			thread.start();
	}
}
