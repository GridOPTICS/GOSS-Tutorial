//package pnnl.goss.tutorial.launchers;
//
//import java.io.Serializable;
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import pnnl.goss.core.Request;
//import pnnl.goss.core.UploadRequest;
//import pnnl.goss.core.client.Client;
//import pnnl.goss.core.client.GossClient;
//import pnnl.goss.core.client.GossClient.PROTOCOL;
//import pnnl.goss.server.core.GossRequestHandlerRegistrationService;
//import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;
//import pnnl.goss.tutorial.impl.PMUAggregatorImpl;
//import pnnl.goss.tutorial.request.TutorialDownloadRequestSync;
//import py4j.GatewayServer;
//
////This is needed so that py4j can make java calls
//@Component
//@Instantiate
//public class PythonJavaGatewayLauncher extends Thread{
//
//	private static Logger log = LoggerFactory
//			.getLogger(PythonJavaGatewayLauncher.class);
//
//	private static GatewayServer gatewayServer;
//	private volatile GossRequestHandlerRegistrationService registrationService;
//	GossClient client = null;
//
//	public PythonJavaGatewayLauncher(@Requires GossRequestHandlerRegistrationService registrationService){
//		try{
//			log.debug("Creating python gateway Launcher: registration service "+registrationService);
//			this.registrationService = registrationService;
//			client = new GossClient(PROTOCOL.OPENWIRE);
//			client.setConfiguration(this.registrationService.getCoreServerConfig());
//		}catch(Exception e){
//			log.error("Failing while creating python gateway launcher", e);
//		}
//	}
//
//	public void startLauncher(){
//		log.debug("Starting Pythong gateway.");
//		new PythonJavaGatewayLauncher(this.registrationService).start();
//	}
//
//	public void stopLauncher(){
//		if (gatewayServer != null){
//			gatewayServer.shutdown();
//			gatewayServer = null;
//		}
//	}
//
////	public static void main(String[] args) {
////		new PythonJavaGatewayLauncher().startLauncher();
////	}
//
//	@Override
//	public void run() {
//		gatewayServer = new GatewayServer(new PythonJavaGatewayLauncher(this.registrationService));
//		gatewayServer.start();
//		System.out.println("Python-Java Gateway Server Started");
//	}
//
//
//	//Needed these because karaf was causing something to happen so python couldn't create them directly
//	public Client getClient(){
//		if(client==null){
//			//Shouldn't need to call this and it will probably fail because it doesn't have a configuration
//			client = new GossClient();
//		}
//
//		return client;
//	}
//	public UploadRequest createUploadRequest(Date timestamp, Double phasor1, Double phasor2, Double difference, String dataType){
//		Serializable data = new PMUPhaseAngleDiffData(timestamp, phasor1, phasor2, difference);
//		return new UploadRequest(data, dataType);
//	}
//	public Request createDownloadRequest(Date timestamp){
//		return new TutorialDownloadRequestSync(timestamp);
//	}
//
//}
