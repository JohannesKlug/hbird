package org.hbird.application.spacedynamics.provided;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.hbird.application.spacedynamics.interfaces.TleServices;
import org.hbird.application.spacedynamics.tle.CzmlPropagationFinishedListener;
import org.hbird.application.spacedynamics.tle.TleCzmlkUtilities;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TleService implements TleServices {
	private static final Logger LOG = LoggerFactory.getLogger(TleService.class);

	private static final int HALF_MINUTE = 30;

	private final File tleFile = new File("tleData.txt");

	public void loadCzmlFile() {
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
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.info("Loaded TLE data from Celestrak NORAD TLE for cubesats");

	}

	@Override
	public void requestAsyncOrbitPropagationCzml(String satelliteName, Frame referenceFrame, int propagationStep, CzmlPropagationFinishedListener finishedListener) throws TleServiceException {
		try {
			TleCzmlkUtilities.asyncCreateCzmlFromTleFile(tleFile, finishedListener, satelliteName, referenceFrame, propagationStep);
		}
		catch (IOException | OrekitException e) {
			throw new TleServiceException("Could not carry out orbit propagation due to " + e.getMessage(), e);
		}
	}

	@Override
	public String requestSyncOrbitPropagationCzml(String satelliteName) throws TleServiceException {
		return TleCzmlkUtilities.syncCreateCzmlFromTleFile(tleFile, satelliteName, FramesFactory.getEME2000(), HALF_MINUTE);
	}

}
