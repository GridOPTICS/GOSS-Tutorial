package pnnl.goss.tutorial.launchers;

import py4j.GatewayServer;

//This is needed so that py4j can make java calls
public class PythonJavaGatewayLauncher {

	
	public static void main(String[] args) {
		 GatewayServer gatewayServer = new GatewayServer(new PythonJavaGatewayLauncher());
	        gatewayServer.start();
	        System.out.println("Python-Java Gateway Server Started");

	}

}
