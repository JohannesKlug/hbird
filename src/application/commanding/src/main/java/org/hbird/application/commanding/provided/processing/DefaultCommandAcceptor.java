package org.hbird.application.commanding.provided.processing;

import org.apache.camel.Produce;
import org.hbird.application.commanding.interfaces.processing.CommandAcceptor;
import org.hbird.application.commanding.interfaces.processing.CommandSender;
import org.hbird.core.commons.tmtc.CommandGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCommandAcceptor implements CommandAcceptor {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultCommandAcceptor.class);

	/**
	 * The commandSenderEndpoint is looked up int he Registry so make sure you have one in your spring or blueprint!
	 */
	@Produce(ref = "commandSenderEndpoint")
	private CommandSender sender;

	public DefaultCommandAcceptor() {
	}

	@Override
	public void acceptCommand(final CommandGroup cmd) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Received command to send: " + cmd);
		}
		sender.sendCommand(cmd);
	}

	public void setSender(final CommandSender sender) {
		this.sender = sender;
	}

}