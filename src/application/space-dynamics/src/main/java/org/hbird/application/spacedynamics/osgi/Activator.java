package org.hbird.application.spacedynamics.osgi;

import java.net.URL;

import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;
import org.orekit.errors.OrekitException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Run when this Hbird space dynamics bundle is started.
 * 
 * @author Mark Doyle
 * 
 */
public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		setupOrekit(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// Nothing to do
	}

	/**
	 * Configured orekit with the necessary orekit data archive. This is bundled within the
	 * hbird space dynamics bundle.
	 * 
	 * @param context
	 *            this bundle's context.
	 */
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
