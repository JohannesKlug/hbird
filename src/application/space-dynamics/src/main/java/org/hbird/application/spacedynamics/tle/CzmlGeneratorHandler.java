package org.hbird.application.spacedynamics.tle;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.joda.time.DateTime;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.PVCoordinates;

final class CzmlGeneratorHandler implements OrekitFixedStepHandler {
	private final PropagationFinishedListener finishedListener;

	private final Frame frame;

	CzmlGeneratorHandler(PropagationFinishedListener finishedListener, Frame frame) {
		this.finishedListener = finishedListener;
		this.frame = frame;
	}

	@Override
	public void init(SpacecraftState spacecraftState, AbsoluteDate date) throws PropagationException {
		// Nothing to do
	}

	@Override
	public void handleStep(SpacecraftState spacecraftState, boolean isLast) throws PropagationException {
		createCzmlTimeTaggedPosition(finishedListener, frame, spacecraftState, isLast);
	}

	private void createCzmlTimeTaggedPosition(final PropagationFinishedListener finishedListener, final Frame frame, SpacecraftState spacecraftState, boolean isLast) {
		PVCoordinates coords;
		try {
			coords = spacecraftState.getPVCoordinates(frame);
		}
		catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		DateTime isoDate;
		try {
			isoDate = new DateTime(spacecraftState.getDate().toDate(TimeScalesFactory.getUTC()));
		}
		catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Vector3D pos = coords.getPosition();
		if (isLast) {
			System.out.println("\"" + isoDate + "\"" + ", " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
			if (finishedListener != null) {
				finishedListener.finished();
			}
		}
		else {
			System.out.println("\"" + isoDate + "\"" + ", " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ",");
		}
	}
}