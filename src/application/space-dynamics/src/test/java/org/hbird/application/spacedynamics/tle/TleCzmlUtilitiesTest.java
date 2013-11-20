package org.hbird.application.spacedynamics.tle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.hbird.application.spacedynamics.tle.TleCzmlkUtilities.PropagationFinishedListener;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;
import org.orekit.errors.OrekitException;

public class TleCzmlUtilitiesTest {
	
	private boolean wait = true;

	@BeforeClass
	public static final void setupOrekit() {
		URL url = TleCzmlUtilitiesTest.class.getResource("/orekit-data.zip");
		DataProvidersManager.getInstance().addProvider(new ZipJarCrawler(new File(url.getPath())));
	}

	@Test
	public void testCreateCzmlFromTleFile() throws IOException, OrekitException, InterruptedException {
		URL url = getClass().getResource("cubesatTle.txt");
		File testDataFile = new File(url.getPath());

		TleCzmlkUtilities.createCzmlFromTleFile(testDataFile, new FinishedListener(), "blah");
		
		while(wait) {
			Thread.sleep(500);
		}
	}
	
	private class FinishedListener implements PropagationFinishedListener {

		@Override
		public void finished() {
			wait = false;
		}
		
	}

}
