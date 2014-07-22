package pnnl.goss.tutorial.launchers;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

import py4j.GatewayServer;

//This is needed so that py4j can make java calls
@Component
@Instantiate
public class PythonJavaGatewayLauncher extends Thread{

	private static GatewayServer gatewayServer;
	
	@Validate
	public void startLauncher(){
		System.out.println("Starting Pythong gateway.");
		new PythonJavaGatewayLauncher().start();
	}
	
	@Invalidate
	public void stopLauncher(){
		if (gatewayServer != null){
			gatewayServer.shutdown();
			gatewayServer = null;
		}
	}
	
	public static void main(String[] args) {
		new PythonJavaGatewayLauncher().startLauncher();
	}

	@Override
	public void run() {
		gatewayServer = new GatewayServer(new PythonJavaGatewayLauncher());
		gatewayServer.start();
		System.out.println("Python-Java Gateway Server Started");
	}
}
