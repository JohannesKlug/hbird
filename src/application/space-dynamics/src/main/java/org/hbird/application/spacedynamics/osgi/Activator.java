package org.hbird.application.spacedynamics.osgi;

import java.net.URL;

import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;
import org.orekit.errors.OrekitException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		setupOrekit(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// Nothing to do
	}

	public static final void setupOrekit(BundleContext context) {
		URL dataUrl = context.getBundle().getResource("/orekit-data.zip");
		try {
			DataProvidersManager.getInstance().addProvider(new ZipJarCrawler(dataUrl));
		}
		catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
