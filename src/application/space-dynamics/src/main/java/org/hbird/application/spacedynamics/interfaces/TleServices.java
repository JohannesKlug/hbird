package org.hbird.application.spacedynamics.interfaces;

import org.hbird.application.spacedynamics.czml.CzmlPropagationFinishedListener;
import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.orekit.frames.Frame;

public interface TleServices {

	void requestAsyncOrbitPropagationCzml(String satelliteName, Frame referenceFrame, int propagationStep, CzmlPropagationFinishedListener finishedListener) throws TleServiceException;

	/**
	 * Provides a CZML String describing an Orbit propagation for the given satellite. The data is provided by the <a
	 * href="http://www.celestrak.com/NORAD/elements/">celestrak</a> TLE catalogue. This service defaults to the EME2000
	 * inertial reference frame and propagates 1 days worth of positions at 30 seconds intervals.
	 * 
	 * @param satelliteName
	 * @return
	 * @throws TleServiceException
	 */
	String requestSyncOrbitPropagationCzml(String satelliteName) throws TleServiceException;
}
