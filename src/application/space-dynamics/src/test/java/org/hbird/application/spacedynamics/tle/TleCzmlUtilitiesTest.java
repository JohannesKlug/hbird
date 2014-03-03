package org.hbird.application.spacedynamics.tle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.hbird.application.spacedynamics.czml.CzmlPropagationFinishedListener;
import org.hbird.application.spacedynamics.czml.CzmlOrbitPropagationCalculator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;

public class TleCzmlUtilitiesTest {

	private static final String STRAND_1_NORAD_ID = "STRAND 1";

	private static final int HALF_MINUTE = 30;

	private boolean wait = true;

	@BeforeClass
	public static final void setupOrekit() {
		final URL url = TleCzmlUtilitiesTest.class.getResource("/orekit-data.zip");
		DataProvidersManager.getInstance().addProvider(new ZipJarCrawler(new File(url.getPath())));
	}

	@Test
	public void testSyncCreateCzmlFromTleFileFixedFrame() {
		final URL url = getClass().getResource("cubesatTle.txt");
		final File testDataFile = new File(url.getPath());

		String czml = CzmlOrbitPropagationCalculator.syncCreateCzmlFromTleFile(testDataFile, STRAND_1_NORAD_ID, FramesFactory.getEME2000(), HALF_MINUTE);

		System.out.println(czml);
	}

	@Test
	public void testAsyncCreateCzmlFromTleFileInertialFrame() throws IOException, OrekitException, InterruptedException {
		final URL url = getClass().getResource("cubesatTle.txt");
		final File testDataFile = new File(url.getPath());

		CzmlOrbitPropagationCalculator.asyncCreateCzmlFromTleFile(testDataFile, new FinishedListener(), STRAND_1_NORAD_ID, FramesFactory.getEME2000(), HALF_MINUTE);

		while (wait) {
			Thread.sleep(500);
		}
	}

	@Test
	public void testAsynCreateCzmlFromTleFileFixedFrame() throws IOException, OrekitException, InterruptedException {
		final URL url = getClass().getResource("cubesatTle.txt");
		final File testDataFile = new File(url.getPath());

		CzmlOrbitPropagationCalculator.asyncCreateCzmlFromTleFile(testDataFile, new FinishedListener(), STRAND_1_NORAD_ID, FramesFactory.getITRF2008(), HALF_MINUTE);

		while (wait) {
			Thread.sleep(500);
		}
	}

	private class FinishedListener implements CzmlPropagationFinishedListener {

		@Override
		public void finished(String czml) {
			wait = false;
			System.out.println(czml);
		}

	}

}
