package pnnl.goss.tutorial.response;

import java.io.Serializable;

import pnnl.goss.core.Response;
import pnnl.goss.core.GossResponseEvent;

public class GossTutorialResponseEvent implements GossResponseEvent {

	@Override
	public void onMessage(Serializable response) {
		// For now do nothing, maybe add some logging
	}

}
