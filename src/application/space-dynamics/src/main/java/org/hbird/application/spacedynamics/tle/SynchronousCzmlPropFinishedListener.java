package org.hbird.application.spacedynamics.tle;

public class SynchronousCzmlPropFinishedListener implements CzmlPropagationFinishedListener {
	boolean wait = true;
	private String czml;

	@Override
	public void finished(String czml) {
		this.czml = czml;
		wait = false;
	}

	public boolean isWaiting() {
		return wait;
	}

	public String getCzml() {
		return this.czml;
	}
}