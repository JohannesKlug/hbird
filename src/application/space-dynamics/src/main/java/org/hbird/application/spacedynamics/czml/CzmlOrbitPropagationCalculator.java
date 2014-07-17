package org.hbird.application.spacedynamics.czml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.EventDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * 
 * 
 * @author Mark Doyle
 * 
 */
public class CzmlOrbitPropagationCalculator {

	/** The remainder given by the modulus when we have read a TLE for a satellite */
	private static final int SAT_NAME_LINE_ORDINAL = 0;

	/**
	 * The {@link Propagator} used in CZML generation.
	 */
	private Propagator propagator;

	/**
	 * Any {@link EventDetector}s to be used in the propagation.
	 */
	private List<EventDetector> eventDetectors;

	/**
	 * DEfault constructor which the propagators, detectors e can be injected or added as services.
	 */
	public CzmlOrbitPropagationCalculator() {
		// Nothing to do.
	}

	public CzmlOrbitPropagationCalculator(Propagator propagator) {
		this.propagator = propagator;
	}

	public CzmlOrbitPropagationCalculator(Propagator propagator, List<EventDetector> eventDetectors) {
		this.propagator = propagator;
		this.eventDetectors = eventDetectors;
	}

	/**
	 * 
	 * @param tleFile
	 * @param satNames
	 * @return
	 * @throws IOException
	 * @throws OrekitException
	 */
	public final void asyncCreateCzml(final CzmlPropagationFinishedListener finishedListener, String satelliteName, Frame frame, int propagationStep) throws IOException, OrekitException {
		final DateTime nowUtc = DateTime.now(DateTimeZone.UTC);
		final DateTime tomorrowUtc = nowUtc.plusDays(1);

		final AbsoluteDate now = new AbsoluteDate(nowUtc.toDate(), TimeScalesFactory.getUTC());
		final AbsoluteDate tomorrow = new AbsoluteDate(tomorrowUtc.toDate(), TimeScalesFactory.getUTC());

		propagator.setMasterMode(propagationStep, new CzmlGeneratorHandler(finishedListener, frame, satelliteName));
		addEventDetectorsToPropagator();

		propagator.propagate(now, tomorrow);
	}

	private void addEventDetectorsToPropagator() {
		for (EventDetector detector : eventDetectors) {
			propagator.addEventDetector(detector);
		}
	}

	/**
	 * 
	 * @param tleFile
	 * @param satNames
	 * @return
	 * @throws IOException
	 * @throws OrekitException
	 */
	public static final void asyncCreateCzmlFromTleFile(File tleFile, final CzmlPropagationFinishedListener finishedListener, String satelliteName, Frame frame, int propagationStep)
			throws IOException, OrekitException {
		final BufferedReader bufRead = new BufferedReader(new FileReader(tleFile));

		int count = 0;
		String line = null;

		while ((line = bufRead.readLine()) != null) {
			final int ordinal = count++ % 3;
			if (ordinal == SAT_NAME_LINE_ORDINAL) {
				final String satName = line.trim();
				if (!satName.contains(satelliteName)) {
					continue;
				}

				final String tleLine1 = line = bufRead.readLine();
				final String tleLine2 = line = bufRead.readLine();

				final TLE tle = new TLE(tleLine1, tleLine2);

				final TLEPropagator proper = TLEPropagator.selectExtrapolator(tle);

				final DateTime nowUtc = DateTime.now(DateTimeZone.UTC);
				final DateTime tomorrowUtc = nowUtc.plusDays(1);

				final AbsoluteDate now = new AbsoluteDate(nowUtc.toDate(), TimeScalesFactory.getUTC());
				final AbsoluteDate tomorrow = new AbsoluteDate(tomorrowUtc.toDate(), TimeScalesFactory.getUTC());

				proper.setMasterMode(propagationStep, new CzmlGeneratorHandler(finishedListener, frame, satelliteName));

				proper.propagate(now, tomorrow);

				count += 2;
			}
		}

		bufRead.close();
	}

	/**
	 * Carries out the Propagation but waits for it to finish before returning the CZML String. Clients do not need to
	 * provide their own listener for this method.
	 * 
	 * @param tleFile
	 * @param satelliteName
	 * @param eme2000
	 * @param halfMinute
	 * @return
	 */
	public static String syncCreateCzmlFromTleFile(File tleFile, String satelliteName, Frame frame, int propagationIntervalStep) {

		SynchronousCzmlPropFinishedListener propFinishedListener = new SynchronousCzmlPropFinishedListener();

		try {
			asyncCreateCzmlFromTleFile(tleFile, propFinishedListener, satelliteName, frame, propagationIntervalStep);
		}
		catch (IOException | OrekitException e1) {
			e1.printStackTrace();
		}

		while (propFinishedListener.isWaiting()) {
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return propFinishedListener.getCzml();
	}

	public Propagator getPropagator() {
		return propagator;
	}

	public void setPropagator(Propagator propagator) {
		this.propagator = propagator;
	}

	public void addEventDetector(EventDetector detector) {
		if (eventDetectors == null) {
			eventDetectors = new ArrayList<>();
		}
		eventDetectors.add(detector);
	}

}
