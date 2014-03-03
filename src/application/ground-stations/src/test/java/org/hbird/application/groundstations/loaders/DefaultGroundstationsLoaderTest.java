package org.hbird.application.groundstations.loaders;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.List;

import org.hbird.application.groundstations.Groundstation;
import org.hbird.application.groundstations.exceptions.InvalidGroundstationKml;
import org.junit.Test;

public class DefaultGroundstationsLoaderTest {

	@Test
	public void test() throws InvalidGroundstationKml, URISyntaxException {
		List<Groundstation> actual = DefaultGroundstationsLoader.loadDefaultGroundstations();
		assertEquals("There should be two ground stations loaded.", actual.size(), 2);

		Groundstation station = actual.get(0);
		assertEquals(station.getName(), "CGI Darmstadt - GS");
		assertEquals(station.getDescription(), "CGI Darmstadt Groundstation");
		assertEquals(station.getAlititude(), 0.0, 0.0);
		assertEquals(station.getLongitudeDegrees(), 8.635179810225964, 0.0);
		assertEquals(station.getLatitudeDegrees(), 49.87154414773676, 0.0);

		station = actual.get(1);
		assertEquals(station.getName(), "Kiruna, Sweden (KIS)");
		assertEquals(station.getAlititude(), 0.0, 0.0);
		assertEquals(station.getLongitudeDegrees(), 21.0622, 0.0);
		assertEquals(station.getLatitudeDegrees(), 67.8767, 0.0);
	}
}
