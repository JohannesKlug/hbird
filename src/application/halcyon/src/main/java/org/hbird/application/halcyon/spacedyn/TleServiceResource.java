package org.hbird.application.halcyon.spacedyn;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hbird.application.halcyon.osgi.OsgiReady;
import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.hbird.application.spacedynamics.interfaces.TleServices;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/navigation/tle")
public class TleServiceResource extends OsgiReady {

	private static final String TLE_OSGI_SERVICE = "org.hbird.application.spacedynamics.interfaces.TleServices";

	public TleServiceResource() {
		super(TLE_OSGI_SERVICE);
	}

	@Path("/propagate/czml/{satelliteName}")
	@GET
	@Produces(APPLICATION_JSON)
	public String generateCzmlOrbitPropagation(@PathParam("satelliteName") String satelliteName) {
		final TleServices tleService = (TleServices) getServiceTracker().getService();
		if (tleService != null) {
			try {
				String czml = tleService.requestSyncOrbitPropagationCzml(satelliteName);
				return czml;
			}
			catch (TleServiceException e) {
				e.printStackTrace();
				throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("A " + TLE_OSGI_SERVICE + " is unavailable.").build());
	}

}
