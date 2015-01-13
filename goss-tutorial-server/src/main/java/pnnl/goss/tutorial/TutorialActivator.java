package pnnl.goss.tutorial;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.Client;
import pnnl.goss.tutorial.launchers.GeneratorLauncher;

public class TutorialActivator implements BundleActivator {
    private static Logger log = LoggerFactory.getLogger(TutorialActivator.class);
    private GeneratorLauncher launcher;
    private ServiceTracker clientTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        log.debug("Bundle starting!");
        clientTracker = new ServiceTracker(context, Client.class.getName(), null);
        clientTracker.open();
        Client client =(Client)clientTracker.getService();
        //launcher = new GeneratorLauncher();
        launcher = new GeneratorLauncher(client);
        launcher.startLauncher();
    }

    private Client getClient(){
        return (Client)clientTracker.getService();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.debug("Bundle stopping!");
        launcher.stopLauncher();
        clientTracker.close();
        launcher = null;

    }

}
