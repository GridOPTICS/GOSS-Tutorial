package pnnl.goss.tutorial.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping;
import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class TutorialWebActivator implements BundleActivator{
    private static Logger log = LoggerFactory.getLogger(TutorialWebActivator.class);
    ServiceRegistration resourceService;
	@Override
	public void start(BundleContext context) throws Exception {
        log.debug(" Bundle starting! "+getClass());
		
        DefaultResourceMapping resourceMapping = new DefaultResourceMapping();
        resourceMapping.setAlias( "/tutorial" );
        resourceMapping.setPath( "/" );
        resourceService = context.registerService( ResourceMapping.class.getName(), resourceMapping, null );

	}

	@Override
	public void stop(BundleContext context) throws Exception {
        log.debug("Bundle stopping! "+getClass());
		if(resourceService!=null){
			resourceService.unregister();
		}
	}

}
