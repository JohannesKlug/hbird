package org.hbird.application.groundstations;

/**
 * A simple bean describing a Ground station.
 * 
 * @author Mark Doyle
 * 
 */
public class Groundstation {

	private String name;

	private String description;

	private double longitudeDegrees;

	private double latitudeDegrees;

	private double altitude;

	/**
	 * Default constructor, mainly used by frameworks.
	 */
	public Groundstation() {
		// Do nothing, this is so you can set the properties later.
	}

	/**
	 * Creates a fully initialised {@link Groundstation}.
	 * 
	 * @param name
	 *            the groundstations name
	 * @param description
	 *            a description of the GroundStation
	 * @param longitudeDegrees
	 *            the groundstations longitude
	 * @param latitudeDegrees
	 *            the groundstations latitude
	 * @param heightMetres
	 *            the altitude of the groundstation in metres.
	 */
	public Groundstation(String name, String description, double longitudeDegrees, double latitudeDegrees, double heightMetres) {
		this.name = name;
		this.description = description;
		this.longitudeDegrees = longitudeDegrees;
		this.latitudeDegrees = latitudeDegrees;
		this.altitude = heightMetres;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLongitudeDegrees() {
		return longitudeDegrees;
	}

	public void setLongitudeDegrees(double longitudeDegrees) {
		this.longitudeDegrees = longitudeDegrees;
	}

	public double getLatitudeDegrees() {
		return latitudeDegrees;
	}

	public void setLatitudeDegrees(double latitudeDegrees) {
		this.latitudeDegrees = latitudeDegrees;
	}

	public double getAlititude() {
		return altitude;
	}

	public void setHeightMetres(double heightMetres) {
		this.altitude = heightMetres;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Groundstation [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", longitudeDegrees=");
		builder.append(longitudeDegrees);
		builder.append(", latitudeDegrees=");
		builder.append(latitudeDegrees);
		builder.append(", altitude=");
		builder.append(altitude);
		builder.append("]");
		return builder.toString();
	}

}
