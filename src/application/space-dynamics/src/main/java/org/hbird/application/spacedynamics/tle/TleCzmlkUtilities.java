package org.hbird.application.spacedynamics.tle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.PVCoordinates;

/**
 * 
 * 
 * @author Mark Doyle
 * 
 */
public class TleCzmlkUtilities {

	private static final int HALF_MINUTE = 30;
	
	/** The remainder given by the modulus when we have read a TLE for a satellite */
	private static final int SAT_NAME_LINE_ORDINAL = 0;

	/**
	 * 
	 * @param tleFile
	 * @param satNames
	 * @return
	 * @throws IOException
	 * @throws OrekitException
	 */
	public static final String createCzmlFromTleFile(File tleFile, final PropagationFinishedListener finishedListener, String... satNames) throws IOException,
			OrekitException {
		String czml = null;

		BufferedReader bufRead = new BufferedReader(new FileReader(tleFile));

		int count = 0;
		String line = null;

		while ((line = bufRead.readLine()) != null) {
			int ordinal = count++ % 3;
			if (ordinal == SAT_NAME_LINE_ORDINAL) {
				String satName = line.trim();
				if (!satName.contains("STRAND")) {
					continue;
				}

				String tleLine1 = line = bufRead.readLine();
				String tleLine2 = line = bufRead.readLine();

				TLE tle = new TLE(tleLine1, tleLine2);

				final Frame frame = FramesFactory.getITRF2008();
				final TLEPropagator proper = TLEPropagator.selectExtrapolator(tle);

				DateTime nowUtc = DateTime.now(DateTimeZone.UTC);
				DateTime tomorrowUtc = nowUtc.plusDays(1);

				final AbsoluteDate now = new AbsoluteDate(nowUtc.toDate(), TimeScalesFactory.getUTC());
				final AbsoluteDate tomorrow = new AbsoluteDate(tomorrowUtc.toDate(), TimeScalesFactory.getUTC());

				proper.setMasterMode(HALF_MINUTE, new OrekitFixedStepHandler() {

					@Override
					public void init(SpacecraftState spacecraftState, AbsoluteDate date) throws PropagationException {
//						createCzmlTimeTaggedPosition(finishedListener, frame, spacecraftState, false);
					}

					@Override
					public void handleStep(SpacecraftState spacecraftState, boolean isLast) throws PropagationException {
						createCzmlTimeTaggedPosition(finishedListener, frame, spacecraftState, isLast);
					}

					private void createCzmlTimeTaggedPosition(final PropagationFinishedListener finishedListener, final Frame frame,
							SpacecraftState spacecraftState, boolean isLast) {
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
				});

				proper.propagate(now, tomorrow);

				count += 2;
			}
		}

		bufRead.close();

		return czml;
	}

	public interface PropagationFinishedListener {
		void finished();
	}
}
