package org.hbird.application.groundstations.services;

import org.hbird.application.groundstations.exceptions.InvalidGroundstationKml;
import org.orekit.frames.Frame;

/**
 * Defines external services for CZML activties related to ground-stations.
 * 
 * @author Mark
 * 
 */
public interface GroundstationCzmlServices {

	String generateCzmlFromDegrees(String gsName, double longitudeDegree, double latitudeDegree, double heightMetres);

	String generateCzmlFromRadians(String gsName, double longitudeRadian, double latitudeRadian, double heightMetres);

	String generateCzmlFromCartesian(Frame referenceFrame, double x, double y, double z);

	String generateDefaultGroundStationsAsCzml() throws InvalidGroundstationKml;

}
