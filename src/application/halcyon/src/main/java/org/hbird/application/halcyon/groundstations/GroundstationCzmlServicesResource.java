package org.hbird.application.halcyon.groundstations;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.hbird.application.groundstations.czml.GroundstationCzmlUtilities;
import org.hbird.application.groundstations.exceptions.InvalidGroundstationKml;
import org.hbird.application.groundstations.loaders.DefaultGroundstationsLoader;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/groundstations")
public class GroundstationCzmlServicesResource {

	@Path("/czml/default")
	@GET
	@Produces(APPLICATION_JSON)
	public String generateCzmlOrbitPropagation() {
		try {
			return GroundstationCzmlUtilities.generateCzmlFromGroundstations(DefaultGroundstationsLoader.loadDefaultGroundstations());
		}
		catch (InvalidGroundstationKml e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}
