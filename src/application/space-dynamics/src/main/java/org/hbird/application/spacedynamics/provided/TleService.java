package org.hbird.application.spacedynamics.provided;

import java.io.File;
import java.io.IOException;

import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.hbird.application.spacedynamics.interfaces.TleServices;
import org.hbird.application.spacedynamics.tle.CzmlPropagationFinishedListener;
import org.hbird.application.spacedynamics.tle.TleCzmlkUtilities;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;

public class TleService implements TleServices {

	private static final int HALF_MINUTE = 30;
	File tleFile;

	private void loadCzmlFile() {
		// TODO
		tleFile = new File("");
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
