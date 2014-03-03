package org.hbird.application.groundstations.util;

import org.hbird.application.groundstations.Groundstation;
import org.orekit.bodies.GeodeticPoint;

public abstract class GroundstationUtils {

	private GroundstationUtils() {
		// Utils class
	}

	public static final GeodeticPoint createGeodeticPointFrom(Groundstation gs) {
		return createGeodeticPointFrom(gs.getLongitudeDegrees(), gs.getLatitudeDegrees(), gs.getAlititude());
	}

	private static final GeodeticPoint createGeodeticPointFrom(double longitude, double latitude, double altitude) {
		return new GeodeticPoint(latitude, longitude, altitude);
	}
}
