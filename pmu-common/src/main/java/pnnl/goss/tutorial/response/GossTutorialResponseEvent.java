package pnnl.goss.tutorial.response;

import pnnl.goss.core.Response;
import pnnl.goss.core.client.GossResponseEvent;

public class GossTutorialResponseEvent implements GossResponseEvent {

	@Override
	public void onMessage(Response response) {
		// For now do nothing, maybe add some logging
	}

}
