package org.hbird.application.spacedynamics.provided;

import java.io.File;
import java.io.IOException;

import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.hbird.application.spacedynamics.interfaces.TleServices;
import org.hbird.application.spacedynamics.tle.PropagationFinishedListener;
import org.hbird.application.spacedynamics.tle.TleCzmlkUtilities;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;

public class TleService implements TleServices {

	File tleFile;

	@Override
	public String requestOrbitPropagation(PropagationFinishedListener finishedListener, String satelliteName, Frame referenceFrame, int propagationStep) throws TleServiceException {
		try {
			return TleCzmlkUtilities.createCzmlFromTleFile(tleFile, finishedListener, satelliteName, referenceFrame, propagationStep);
		}
		catch (IOException | OrekitException e) {
			throw new TleServiceException("Could not carry out orbit propagation due to " + e.getMessage(), e);
		}
	}

	public File getTleFile() {
		return tleFile;
	}

	public void setTleFile(File tleFile) {
		this.tleFile = tleFile;
	}

}
