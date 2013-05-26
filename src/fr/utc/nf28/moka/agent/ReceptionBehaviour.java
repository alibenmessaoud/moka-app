package fr.utc.nf28.moka.agent;

import android.util.Log;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static fr.utc.nf28.moka.util.LogUtils.makeLogTag;

public class ReceptionBehaviour extends CyclicBehaviour {

	/**
	 * LogCat tag
	 */
	private static final String TAG = makeLogTag(ReceptionBehaviour.class);

	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		if (message != null) {
			final String content = message.getContent();
			((AndroidAgent)myAgent).sendBroadcastMessage(content);
			Log.i(TAG, content);
		} else {
			block();
		}
	}
}
