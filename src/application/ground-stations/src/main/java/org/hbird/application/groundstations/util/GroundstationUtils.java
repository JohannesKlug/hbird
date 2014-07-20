package org.hbird.application.groundstations.util;

import org.hbird.application.groundstations.Groundstation;
import org.orekit.bodies.GeodeticPoint;

public abstract class GroundstationUtils {

	private GroundstationUtils() {
		// Utils class
	}

	public static final GeodeticPoint createGeodeticPointFrom(Groundstation gs) {
		return new GeodeticPoint(gs.getLatitudeDegrees(), gs.getLongitudeDegrees(), gs.getAlititude());
	}
}
