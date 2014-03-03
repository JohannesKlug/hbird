package org.hbird.application.groundstations.providedservices;

import org.hbird.application.groundstations.czml.GroundstationCzmlUtilities;
import org.hbird.application.groundstations.exceptions.InvalidGroundstationKml;
import org.hbird.application.groundstations.loaders.DefaultGroundstationsLoader;
import org.hbird.application.groundstations.services.GroundstationCzmlServices;
import org.orekit.frames.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbirdCzmlService implements GroundstationCzmlServices {
	private static final Logger LOG = LoggerFactory.getLogger(HbirdCzmlService.class);

	@Override
	public String generateCzmlFromDegrees(String gsName, double longitudeDegree, double latitudeDegree, double heightMetres) {
		throw new UnsupportedOperationException("Not implemented yet, sorry!");
	}

	@Override
	public String generateCzmlFromRadians(String gsName, double longitudeRadian, double latitudeRadian, double heightMetres) {
		throw new UnsupportedOperationException("Not implemented yet, sorry!");
	}

	@Override
	public String generateCzmlFromCartesian(Frame referenceFrame, double x, double y, double z) {
		throw new UnsupportedOperationException("Not implemented yet, sorry!");
	}

	@Override
	public String generateDefaultGroundStationsAsCzml() throws InvalidGroundstationKml {
		return GroundstationCzmlUtilities.generateCzmlFromGroundstations(DefaultGroundstationsLoader.loadDefaultGroundstations());
	}
}
