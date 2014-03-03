package org.hbird.application.groundstations;

import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;

public class KmlGroundstationFactory {

	public static final Groundstation createGroundstationFrom(Placemark placemark) {
		// NASTY Java KML library API
		Point point = (Point) placemark.getGeometry();
		List<Coordinate> coords = point.getCoordinates();
		Coordinate gsPos = coords.get(0);

		return new Groundstation(placemark.getName(), placemark.getDescription(), gsPos.getLongitude(), gsPos.getLatitude(), gsPos.getAltitude());
	}
}
