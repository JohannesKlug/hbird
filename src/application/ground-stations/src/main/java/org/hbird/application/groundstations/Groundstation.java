package org.hbird.application.groundstations;

public class Groundstation {

	private String name;

	private String description;

	private double longitudeDegrees;

	private double latitudeDegrees;

	private double altitude;

	public Groundstation() {
		// Do nothing, this is so you can set the properties later.
	}

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
