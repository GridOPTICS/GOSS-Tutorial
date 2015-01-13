package pnnl.goss.tutorial;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.Client;
import pnnl.goss.tutorial.launchers.AggregatorLauncher;
import pnnl.goss.tutorial.launchers.GeneratorLauncher;

public class TutorialActivator implements BundleActivator {
    private static Logger log = LoggerFactory.getLogger(TutorialActivator.class);
    private GeneratorLauncher genrationLauncher;
    private AggregatorLauncher aggregationLauncher;
    private ServiceTracker clientTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        log.debug("Bundle starting!");
        clientTracker = new ServiceTracker(context, Client.class.getName(), null);
        clientTracker.open();
        Client client =(Client)clientTracker.getService();
        Client client2 = (Client)clientTracker.getService();
        assert client != client2;
        //launcher = new GeneratorLauncher();
        genrationLauncher = new GeneratorLauncher(client);
        genrationLauncher.startLauncher();
        aggregationLauncher = new AggregatorLauncher(client);
        aggregationLauncher.startLauncher();
    }

    private Client getClient(){
        return (Client)clientTracker.getService();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.debug("Bundle stopping!");
        genrationLauncher.stopLauncher();
        aggregationLauncher.stopLauncher();
        clientTracker.close();
        genrationLauncher = null;

    }

}
