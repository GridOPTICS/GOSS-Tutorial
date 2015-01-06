package pnnl.goss.tutorial.launchers;

import java.util.Dictionary;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Updated;
import org.apache.felix.ipojo.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.server.core.GossDataServices;
import pnnl.goss.server.core.GossRequestHandlerRegistrationService;
import pnnl.goss.server.core.internal.BasicDataSourceCreator;
import pnnl.goss.tutorial.handlers.TutorialDesktopDownloadHandler;
import pnnl.goss.tutorial.handlers.TutorialDesktopHandler;
import pnnl.goss.tutorial.request.TutorialDownloadRequestAsync;
import pnnl.goss.tutorial.request.TutorialDownloadRequestSync;
import static pnnl.goss.core.GossCoreContants.PROP_DATASOURCES_CONFIG;


@Component(managedservice = PROP_DATASOURCES_CONFIG)
@Instantiate
public class PMUTutorialActivator {
	@Requires
	private GossRequestHandlerRegistrationService registrationService;
	
	
	public static final String PROP_TUTORIALDB_DATASERVICE = "goss/tutorialdb";
	public static final String PROP_TUTORIALDB_USER = "tutorialdb.user";
	public static final String PROP_TUTORIALDB_PASSWORD = "tutorialdb.password";
	public static final String PROP_TUTORIALDB_URI = "tutorialdb.uri";
	
	
	/**
	 * <p>
	 * Add logging to the class so that we can debug things effectively after
	 * deployment.
	 * </p>
	 */
	private static Logger log = LoggerFactory
			.getLogger(PMUTutorialActivator.class);
	
	private GossDataServices dataServices;
	@Requires
	private BasicDataSourceCreator datasourceCreator;

	private String user;
	private String password;
	private String uri;
	
	
	public PMUTutorialActivator(
			@Requires GossRequestHandlerRegistrationService registrationService,
			@Requires GossDataServices dataServices){
		this.registrationService = registrationService;
		this.dataServices = dataServices;
		log.debug("Constructing activator");
	}
	
	
	
	private void registerDataService() {
		if (dataServices != null) {
			if (!dataServices.contains(PROP_TUTORIALDB_DATASERVICE)) {
				log.debug("Attempting to register dataservice: "
						+ PROP_TUTORIALDB_DATASERVICE);
				if (datasourceCreator == null){
					datasourceCreator = new BasicDataSourceCreator();
				}
				if (datasourceCreator != null){
					if(uri!=null){
						try {
							BasicDataSource dataSource =datasourceCreator.create(uri, user, password);
							if(dataSource!=null){
							
							dataServices.registerData(PROP_TUTORIALDB_DATASERVICE,
									dataSource);
							} else {
								log.warn("Data source not created for uri "+uri+" and user "+user);
							}
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					} else {
						log.warn("Could not create datasource, uri is null!");
					}
				}
				else{
					log.error("datasourceCreator is null!");
				}
			}
		} else {
			log.error("dataServices is null!");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Updated
	public void update(Dictionary config) {
		log.debug("updating");
		user = (String) config.get(PROP_TUTORIALDB_USER);
		password = (String) config.get(PROP_TUTORIALDB_PASSWORD);
		uri = (String) config.get(PROP_TUTORIALDB_URI);

		log.debug("updated uri: " + uri + "\n\tuser:" + user);
		registerDataService();
	}
	
	
	
	@Validate
	public void startActivator(){
		System.out.println("Starting bundle "+getClass().getName());
		try{
			if (registrationService != null) {
			//Add handlers
			registrationService.addUploadHandlerMapping("Tutorial", TutorialDesktopHandler.class);
			registrationService.addHandlerMapping(TutorialDownloadRequestSync.class, TutorialDesktopDownloadHandler.class);
			registrationService.addHandlerMapping(TutorialDownloadRequestAsync.class, TutorialDesktopDownloadHandler.class);
			} else {
				log.error(GossRequestHandlerRegistrationService.class.getName()
						+ " not found!");
			}
		} catch (Exception e) {
			log.error("error during starting of bundle", e);
		} finally {
			if (dataServices != null) {
				dataServices.unRegisterData(PROP_TUTORIALDB_DATASERVICE);
			}
		}
	}
	@Invalidate
	public void stopActivator(){
		try {
			log.info("Stopping the bundle" + this.getClass().getName());
			if (registrationService != null) {
				registrationService.removeHandlerMapping(TutorialDownloadRequestSync.class);
				registrationService.removeHandlerMapping(TutorialDownloadRequestAsync.class);
			}

		} catch (Exception e) {
			log.error("error during stopping of bundle", e);
		} finally {
			if (dataServices != null) {
				dataServices.unRegisterData(PROP_TUTORIALDB_DATASERVICE);
			}
		}
	}
}
