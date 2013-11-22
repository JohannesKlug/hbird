package org.hbird.application.spacedynamics.tle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

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
	public static final String createCzmlFromTleFile(File tleFile, final PropagationFinishedListener finishedListener, String satelliteName, Frame frame) throws IOException, OrekitException {
		final String czml = null;

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

				proper.setMasterMode(HALF_MINUTE, new CzmlGeneratorHandler(finishedListener, frame, satelliteName));

				proper.propagate(now, tomorrow);

				count += 2;
			}
		}

		bufRead.close();

		return czml;
	}
}
