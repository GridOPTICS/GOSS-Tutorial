package pnnl.goss.tutorial.web;

//import java.util.Dictionary;
//import java.util.Hashtable;
//
//import javax.servlet.Servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping;
import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class TutorialWebActivator implements BundleActivator{
    private static Logger log = LoggerFactory.getLogger(TutorialWebActivator.class);
    
	@Override
	public void start(BundleContext context) throws Exception {
        log.debug(" Bundle starting! "+getClass());
//        Dictionary props = new Hashtable();
//        props.put( "alias", "/tutorialServlet" );
//        props.put("servlet-name", "Tutorial Servlet");
//        context.registerService(Servlet.class.getName(), new TutorialServlet(), props);
		
        DefaultResourceMapping resourceMapping = new DefaultResourceMapping();
        resourceMapping.setAlias( "/tutorial" );
        resourceMapping.setPath( "/" );
        context.registerService( ResourceMapping.class.getName(), resourceMapping, null );

	}

	@Override
	public void stop(BundleContext context) throws Exception {
        log.debug("Bundle starting! "+getClass());

		
	}

}
