package org.hbird.application.groundstations.providedservices;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hbird.application.groundstations.Groundstation;
import org.hbird.application.groundstations.exceptions.InvalidGroundstationKml;
import org.hbird.application.groundstations.exceptions.UnknownGroundStationException;
import org.hbird.application.groundstations.loaders.DefaultGroundstationsLoader;
import org.hbird.application.groundstations.services.GroundstationServices;

/**
 * The model can currently handle the {@link GroundstationServices} provision since nothing complex is giong on yet.
 * 
 * @author Mark
 * 
 */
public class InMemoryGroundstationsModel implements GroundstationServices {

	List<Groundstation> stations;

	public void loadDefaults() {
		try {
			stations = DefaultGroundstationsLoader.loadDefaultGroundstations();
		}
		catch (InvalidGroundstationKml e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<Groundstation> getAllGroundstations() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public Groundstation getGroundstation(String gsName) throws UnknownGroundStationException {
		for (Groundstation gs : stations) {
			if (StringUtils.equals(gsName, gs.getName())) {
				return gs;
			}
		}
		throw new UnknownGroundStationException(gsName);
	}

	public void addStation(Groundstation station) {
		if (stations == null) {
			stations = new ArrayList<>();
		}
		stations.add(station);
	}

}
