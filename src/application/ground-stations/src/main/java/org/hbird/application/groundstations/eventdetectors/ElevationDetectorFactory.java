package org.hbird.application.groundstations.eventdetectors;

import static org.hbird.application.groundstations.util.GroundstationUtils.createGeodeticPointFrom;
import static org.orekit.propagation.events.handlers.EventHandler.Action.CONTINUE;
import static org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
import static org.orekit.utils.Constants.WGS84_EARTH_FLATTENING;

import org.hbird.application.groundstations.Groundstation;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.handlers.EventHandler;

public abstract class ElevationDetectorFactory {

	private static final double WGS84_FLATTENING = 1.0 / 298.257223563;

	private static final double WGS84_EQUATORIAL_RADIUS_METRES = 6378137.0;

	private ElevationDetectorFactory() {
		// Utility class.
	}

	public static final ElevationDetector createFrom(Groundstation station) throws OrekitException {
		GeodeticPoint point = createGeodeticPointFrom(station);
		BodyShape earth = new OneAxisEllipsoid(WGS84_EARTH_EQUATORIAL_RADIUS, WGS84_EARTH_FLATTENING, FramesFactory.getITRF2008());

		// Get frame from the point of view of the ground station.
		TopocentricFrame gsTopocentricFrame = new TopocentricFrame(earth, point, station.getName());

		ElevationDetector detector = new ElevationDetector(gsTopocentricFrame).withHandler(new EventHandler<ElevationDetector>() {

			@Override
			public Action eventOccurred(SpacecraftState s, ElevationDetector detector, boolean increasing) throws OrekitException {
				System.out.println("Event occured " + s + ", " + detector);
				return CONTINUE;
			}

			@Override
			public SpacecraftState resetState(ElevationDetector detector, SpacecraftState oldState) throws OrekitException {
				System.out.println("Reset state called occured " + detector);
				// TODO Auto-generated method stub
				return null;
			}

		});
		return detector;
	}
}
