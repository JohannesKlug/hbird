package org.hbird.application.groundstations.czml;

import java.net.URISyntaxException;

import org.hbird.application.groundstations.exceptions.InvalidGroundstationKml;
import org.hbird.application.groundstations.loaders.DefaultGroundstationsLoader;
import org.junit.Test;

public class GroundstationCzmlUtilitiesTest {

	@Test
	public void test() throws InvalidGroundstationKml, URISyntaxException {
		GroundstationCzmlUtilities.generateCzmlFromGroundstations(DefaultGroundstationsLoader.loadDefaultGroundstations());
	}

}
