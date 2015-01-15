package pnnl.goss.tutorial;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.Client;
import pnnl.goss.core.Client.PROTOCOL;
import pnnl.goss.core.ClientFactory;
import pnnl.goss.tutorial.launchers.AggregatorLauncher;
import pnnl.goss.tutorial.launchers.GeneratorLauncher;

public class TutorialActivator implements BundleActivator {
    private static Logger log = LoggerFactory.getLogger(TutorialActivator.class);
    private GeneratorLauncher generationLauncher;
    private AggregatorLauncher aggregationLauncher;
    private ServiceTracker factoryTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        log.debug("Bundle starting!");

        factoryTracker = new ServiceTracker(context, ClientFactory.class.getName(), null);
        factoryTracker.open();

        ClientFactory factory = (ClientFactory) factoryTracker.getService();

        generationLauncher = new GeneratorLauncher(factory.create(PROTOCOL.STOMP));
        generationLauncher.startLauncher();

        aggregationLauncher = new AggregatorLauncher(factory.create(PROTOCOL.STOMP));
        aggregationLauncher.startLauncher();

    }

    private Client getClient(){
        return (Client)factoryTracker.getService();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.debug("Bundle stopping!");
        generationLauncher.stopLauncher();
        aggregationLauncher.stopLauncher();
        factoryTracker.close();
        generationLauncher = null;
        aggregationLauncher = null;

    }

}
