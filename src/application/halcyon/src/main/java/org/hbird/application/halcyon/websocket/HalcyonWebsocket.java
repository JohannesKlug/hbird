package org.hbird.application.halcyon.websocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;
import org.hbird.application.commanding.provided.processing.VerificationUpdate;
import org.hbird.application.halcyon.commanding.CmdVerificationReceiver;
import org.hbird.application.halcyon.tm.LiveTmReceiver;
import org.hbird.application.halcyon.tm.LiveTmWhiteboardDistributer;
import org.hbird.core.spacesystemmodel.tmtc.Parameter;
import org.hbird.core.spacesystemmodel.tmtc.ParameterGroup;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HalcyonWebsocket implements OnTextMessage, LiveTmReceiver, CmdVerificationReceiver {
	private static final Logger LOG = LoggerFactory.getLogger(HalcyonWebsocket.class);

	private Connection connection;

	private ServiceRegistration livetmReceiverRegistration;

	private ServiceRegistration cmdVerfificationReceiverRegistration;

	private boolean filtered = false;

	private final Set<String> parametersActive = new HashSet<>();

	@Override
	public void onOpen(final Connection connection) {
		this.connection = connection;

		// Register this as a LiveTmReceiver so the blueprint instantiated class used in the camel route can
		// route to this (a servlet managed class). Whiteboard pattern.
		final BundleContext bc = FrameworkUtil.getBundle(LiveTmWhiteboardDistributer.class).getBundleContext();
		livetmReceiverRegistration = bc.registerService(LiveTmReceiver.class.getName(), this, null);
		cmdVerfificationReceiverRegistration = bc.registerService(CmdVerificationReceiver.class.getName(), this, null);
	}

	@Override
	public void onClose(final int closeCode, final String message) {
		LOG.info("Websocket is closing with code: " + closeCode + ". " + message);
		livetmReceiverRegistration.unregister();
		cmdVerfificationReceiverRegistration.unregister();
		// TODO tie into OSGI event and log closure due to closeCode
	}

	@Override
	public void onMessage(String data) {
		// FIXME Tied to live tm reception! There is only one websocket though so we should use a proper message to
		// allow for messages from other client pages.
		if (LOG.isTraceEnabled()) {
			LOG.trace("Adding " + data + " to live filtering.");
		}
		// FIXME Not very defensive. Very brittle.
		parametersActive.add(data);
		filtered = true;
	}

	@Override
	public void acceptNewLiveParameterGroup(final ParameterGroup parameterGroup) {
		if (parameterGroup.getAllParameters() != null) {
			for (final Parameter<?> p : parameterGroup.getAllParametersAsList()) {
				if (filtered) {
					if (!parametersActive.contains(p.getQualifiedName())) {
						continue;
					}
				}
				try {
					connection.sendMessage(new HbirdWebsocketMessage(HbirdWebsocketMessageId.LIVE_TM, p).serialiseUpdateToJson());
				}
				catch (final JsonGenerationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (final JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			LOG.warn("Parameters in Parameter group: " + parameterGroup.getQualifiedName() + " are null");
		}
	}

	@Override
	public void verifcationUpdate(VerificationUpdate update) {
		try {
			connection.sendMessage(new HbirdWebsocketMessage(HbirdWebsocketMessageId.CMD_VERIFICATION_UPDATE, update).serialiseUpdateToJson());
			// connection.sendMessage(jsonString);
		}
		catch (IOException e) {
			LOG.error("IOException sending command verification update to websocket. Details: " + e);
		}
	}
}
