package org.hbird.application.groundstations.eventdetectors;

import org.hbird.application.groundstations.Groundstation;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.events.CircularFieldOfViewDetector;

public abstract class CircularFieldOfViewDetectorFactory {

	private static final double FLATTENING = 1.0 / 298.257223563;

	private static final double EQUATORIAL_RADIUS_METRES = 6378137.0;

	private CircularFieldOfViewDetectorFactory() {
		// Utility class.
	}

	public static final CircularFieldOfViewDetector createFrom(Groundstation station) throws OrekitException {
		GeodeticPoint point = new GeodeticPoint(station.getLatitudeDegrees(), station.getLongitudeDegrees(), station.getAlititude());

		BodyShape earth = new OneAxisEllipsoid(EQUATORIAL_RADIUS_METRES, FLATTENING, FramesFactory.getITRF2008());
		TopocentricFrame sta1Frame = new TopocentricFrame(earth, point, station.getName());

	}
}
