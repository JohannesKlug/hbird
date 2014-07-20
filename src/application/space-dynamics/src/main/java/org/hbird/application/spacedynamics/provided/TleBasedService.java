package org.hbird.application.spacedynamics.provided;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.hbird.application.spacedynamics.czml.CzmlOrbitPropagationCalculator;
import org.hbird.application.spacedynamics.czml.CzmlPropagationFinishedListener;
import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.hbird.application.spacedynamics.interfaces.TleServices;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider of {@link TleServices}
 * 
 * @author Mark Doyle
 * 
 */
public class TleBasedService implements TleServices {
	private static final Logger LOG = LoggerFactory.getLogger(TleBasedService.class);

	/** Default propagation interval */
	private static final int HALF_MINUTE = 30;

	private final File tleFile = new File("tleData.txt");

	/**
	 * Loads the Celestrak NORAD TLE catalogue for cubesats from the web.
	 */
	public void loadTleFile() {
		try {
			URL tleUrl = new URL("http://www.celestrak.com/NORAD/elements/cubesat.txt");
			BufferedReader reader = new BufferedReader(new BufferedReader(new InputStreamReader(tleUrl.openStream())));
			BufferedWriter out = new BufferedWriter(new FileWriter(tleFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				out.append(line);
				out.newLine();
			}
			out.close();
			LOG.info("Loaded TLE data from Celestrak NORAD TLE for cubesats");
		}
		catch (IOException e) {
			LOG.error("Could not load TLE data from Celestrak NORAD TLE for cubesats", e);
		}

	}

	@Override
	public void requestAsyncOrbitPropagationCzml(String satelliteName, Frame referenceFrame, int propagationStep, CzmlPropagationFinishedListener finishedListener) throws TleServiceException {
		try {
			CzmlOrbitPropagationCalculator.asyncCreateCzmlFromTleFile(tleFile, finishedListener, satelliteName, referenceFrame, propagationStep);
		}
		catch (IOException | OrekitException e) {
			throw new TleServiceException("Could not carry out orbit propagation due to " + e.getMessage(), e);
		}
	}

	@Override
	public String requestSyncOrbitPropagationCzml(String satelliteName) {
		return requestSyncOrbitPropagationCzml(satelliteName, HALF_MINUTE);
	}

	@Override
	public String requestSyncOrbitPropagationCzml(String satelliteName, int interval) {
		return CzmlOrbitPropagationCalculator.syncCreateCzmlFromTleFile(tleFile, satelliteName, FramesFactory.getEME2000(), interval);
	}

}
