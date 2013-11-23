package org.hbird.application.spacedynamics.interfaces;

import org.hbird.application.spacedynamics.exceptions.TleServiceException;
import org.hbird.application.spacedynamics.tle.PropagationFinishedListener;
import org.orekit.frames.Frame;

public interface TleServices {

	String requestOrbitPropagation(final PropagationFinishedListener finishedListener, String satelliteName, Frame referenceFrame, int propagationStep) throws TleServiceException;
}
