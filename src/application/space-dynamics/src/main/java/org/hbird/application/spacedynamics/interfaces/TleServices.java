package org.hbird.application.spacedynamics.interfaces;

import org.hbird.application.spacedynamics.czml.CzmlPropagationFinishedListener;
import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.orekit.frames.Frame;

/**
 * TLE based space dynamics services offered by this bundle.
 * 
 * @author Mark Doyle
 * 
 */
public interface TleServices {

	void requestAsyncOrbitPropagationCzml(String satelliteName, Frame referenceFrame, int propagationStep, CzmlPropagationFinishedListener finishedListener) throws TleServiceException;

	/**
	 * Provides a CZML String describing an Orbit propagation for the given satellite. The data is provided by the <a
	 * href="http://www.celestrak.com/NORAD/elements/">celestrak</a> TLE catalogue. This service defaults to the EME2000
	 * inertial reference frame and propagates 1 days worth of positions at 30 seconds intervals.
	 * 
	 * @param satelliteName
	 *            the name of the satellite in the celestrak norad catalogue you wish to propogate for.
	 * @return the CZML defining an orbit propagation of the given satellite.
	 * @throws TleServiceException
	 *             TODO
	 */
	String requestSyncOrbitPropagationCzml(String satelliteName) throws TleServiceException;

	/**
	 * Provides a CZML String describing an Orbit propagation for the given satellite. The data is provided by the <a
	 * href="http://www.celestrak.com/NORAD/elements/">celestrak</a> TLE catalogue. This service defaults to the EME2000
	 * inertial reference frame and propagates 1 days worth of positions.
	 * 
	 * @param satelliteName
	 *            the name of the satellite in the celestrak norad catalogue you wish to propogate for.
	 * @param interval
	 *            the interval between propgated position points.
	 * @return the CZML defining an orbit propagation of the given satellite.
	 * @throws TleServiceException
	 *             TODO
	 */
	String requestSyncOrbitPropagationCzml(String satelliteName, int interval) throws TleServiceException;
}
