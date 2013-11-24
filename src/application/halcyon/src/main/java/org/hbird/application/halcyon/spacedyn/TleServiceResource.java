package org.hbird.application.halcyon.spacedyn;

import javax.ws.rs.GET;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hbird.application.halcyon.osgi.OsgiReady;
import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.hbird.application.spacedynamics.interfaces.TleServices;

public class TleServiceResource extends OsgiReady {

	private static final String TLE_OSGI_SERVICE = "org.hbird.application.spacedynamics.interfaces.TleServices";

	public TleServiceResource() {
		super(TLE_OSGI_SERVICE);
	}

	@GET
	public String generateCzmlOrbitPropagation(String satelliteName) {
		final TleServices tleService = (TleServices) getServiceTracker().getService();
		if (tleService != null) {
			try {
				return tleService.requestSyncOrbitPropagationCzml(satelliteName);
			}
			catch (TleServiceException e) {
				e.printStackTrace();
				throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		else {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("A " + TLE_OSGI_SERVICE + " is unavailable.").build());
		}
	}

}
