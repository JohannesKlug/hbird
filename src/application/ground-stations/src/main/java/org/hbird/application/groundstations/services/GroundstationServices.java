package org.hbird.application.groundstations.services;

import java.util.List;

import org.hbird.application.groundstations.Groundstation;
import org.hbird.application.groundstations.exceptions.UnknownGroundStationException;

public interface GroundstationServices {
	List<Groundstation> getAllGroundstations();

	Groundstation getGroundstation(String gsName) throws UnknownGroundStationException;
}
