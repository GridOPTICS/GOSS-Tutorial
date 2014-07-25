package pnnl.goss.tutorial.launchers;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import pnnl.goss.handlers.TutorialDesktopDownloadHandler;
import pnnl.goss.handlers.TutorialDesktopHandler;
import pnnl.goss.server.core.GossRequestHandlerRegistrationService;
import pnnl.goss.tutorial.request.TutorialDownloadRequestAsync;
import pnnl.goss.tutorial.request.TutorialDownloadRequestSync;

@Component
@Instantiate
public class PMUTutorialActivator {
	@Requires
	private GossRequestHandlerRegistrationService service;
	
	@Validate
	public void startActivator(){
		System.out.println("Starting the "+getClass().getName());
		//Add handlers
		service.addUploadHandlerMapping("Tutorial", TutorialDesktopHandler.class);
		service.addHandlerMapping(TutorialDownloadRequestSync.class, TutorialDesktopDownloadHandler.class);
		service.addHandlerMapping(TutorialDownloadRequestAsync.class, TutorialDesktopDownloadHandler.class);
	}
	
	
	@Invalidate
	public void stopActivator(){
		System.out.println("Starting the "+getClass().getName());
		//service.removeHandlerMapping("Tutorial");
		service.removeHandlerMapping(TutorialDownloadRequestSync.class);
		service.removeHandlerMapping(TutorialDownloadRequestAsync.class);
	}
}
