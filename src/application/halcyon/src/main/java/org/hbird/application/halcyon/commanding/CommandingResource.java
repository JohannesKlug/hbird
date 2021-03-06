package org.hbird.application.halcyon.commanding;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hbird.application.commanding.interfaces.processing.CommandAcceptor;
import org.hbird.application.halcyon.osgi.OsgiReady;
import org.hbird.core.commons.tmtc.exceptions.UnknownParameterException;
import org.hbird.core.spacesystemmodel.tmtc.CommandGroup;
import org.hbird.core.spacesystempublisher.exceptions.UnavailableSpaceSystemModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/commanding")
public class CommandingResource extends OsgiReady {
	private final static Logger LOG = LoggerFactory.getLogger(CommandingResource.class);

	private static final String COMMANDING_SERVICE_NAME = "org.hbird.application.commanding.interfaces.processing.CommandAcceptor";

	public CommandingResource() {
		super(COMMANDING_SERVICE_NAME);
	}

	@POST
	@Path("/sendcommand")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendCommand(CommandGroup cmd) {
		final CommandAcceptor cmdService = (CommandAcceptor) getServiceTracker().getService();

		if (cmdService != null) {
			String cmdTransactionId;
			try {
				cmdTransactionId = cmdService.acceptCommand(cmd);
				return Response.status(Status.OK).entity(cmdTransactionId).build();
			}
			catch (UnknownParameterException e) {
				throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
			}
			catch (UnavailableSpaceSystemModelException e) {
				throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

		LOG.error("No commanding service available. Cannot send commmand " + cmd.getQualifiedName());
		return Response.status(Status.BAD_REQUEST).build();
	}
}
