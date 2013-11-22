package org.hbird.application.spacedynamics.tle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.joda.time.DateTime;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.PVCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import cesiumlanguagewriter.BillboardCesiumWriter;
import cesiumlanguagewriter.Cartesian;
import cesiumlanguagewriter.CesiumInterpolationAlgorithm;
import cesiumlanguagewriter.CesiumOutputStream;
import cesiumlanguagewriter.CesiumResourceBehavior;
import cesiumlanguagewriter.CesiumStreamWriter;
import cesiumlanguagewriter.JulianDate;
import cesiumlanguagewriter.LabelCesiumWriter;
import cesiumlanguagewriter.PacketCesiumWriter;
import cesiumlanguagewriter.PathCesiumWriter;
import cesiumlanguagewriter.PositionCesiumWriter;

final class CzmlGeneratorHandler implements OrekitFixedStepHandler {
	private static final int INTERPOLATION_DEGREE = 5;

	private static final double LABEL_SCALE = 0.5;

	private static final Logger LOG = LoggerFactory.getLogger(CzmlGeneratorHandler.class);

	private static final Marker SPACE_DYNAMICS = MarkerFactory.getMarker("SPACE DYNAMICS");

	private final PropagationFinishedListener finishedListener;

	private final String spacecraftName;

	private final Frame frame;

	private final CesiumStreamWriter czmlStreamWriter = new CesiumStreamWriter();

	private final StringWriter stringWriter = new StringWriter();

	private final CesiumOutputStream czmlOutStream = new CesiumOutputStream(stringWriter);

	/** The Packet representing the spacecraft for the complete propagation */
	private PacketCesiumWriter spacecraftPacket;

	private final List<Cartesian> cartesians = new ArrayList<>();

	private final List<JulianDate> dates = new ArrayList<>();

	/**
	 * Constructor, initialises some fields and setsup the czml writers.
	 * 
	 * @param finishedListener
	 *            Any listener that requires notifying when the propagation is finished.
	 * @param frame
	 *            the reference frame we are proagating against.
	 * @param spacecraftName
	 *            the spacecraft name will be used as the CZML packet Label.
	 */
	CzmlGeneratorHandler(PropagationFinishedListener finishedListener, Frame frame, String spacecraftName) {
		this.finishedListener = finishedListener;
		this.frame = frame;
		this.spacecraftName = spacecraftName;
		setupWriters();
	}

	/**
	 * Sets up the CZML writer objects required to generate the CZML.
	 */
	private final void setupWriters() {
		czmlOutStream.setPrettyFormatting(true);
		czmlOutStream.writeStartObject();
	}

	@Override
	public void init(SpacecraftState spacecraftState, AbsoluteDate date) throws PropagationException {
		spacecraftPacket = czmlStreamWriter.openPacket(czmlOutStream);
		spacecraftPacket.writeId(spacecraftName);
		writeAvailability(spacecraftState, date);
		writeLabel();
		writeBillboard();
		writePath();
	}

	private void writePath() {
		PathCesiumWriter pathWriter = spacecraftPacket.getPathWriter();
		pathWriter.open(czmlOutStream);

		pathWriter.writeLeadTimeProperty(0.0);
		pathWriter.writeTrailTimeProperty(600.0);

		pathWriter.writeColorProperty(137, 220, 255, 100);
		pathWriter.writeOutlineColorProperty(111, 177, 237, 100);
		pathWriter.writeWidthProperty(1.0);
		pathWriter.writeOutlineWidthProperty(2.5);

		pathWriter.close();
	}

	private void writeBillboard() {
		final BillboardCesiumWriter billboardWriter = spacecraftPacket.getBillboardWriter();
		billboardWriter.open(czmlOutStream);

		String satPng = null;
		try {
			satPng = createBase64EncodedStringFromURL(this.getClass().getResource("satellite-32x32.png"));
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		billboardWriter.writeImageProperty(satPng, CesiumResourceBehavior.EMBED);

		billboardWriter.close();
	}

	/**
	 * Creates and writes the Label property of the CZML.
	 */
	private void writeLabel() {
		final LabelCesiumWriter labelWriter = spacecraftPacket.getLabelWriter();
		labelWriter.open(czmlOutStream);

		labelWriter.writeTextProperty(spacecraftName);
		labelWriter.writeScaleProperty(LABEL_SCALE);
		labelWriter.writeShowProperty(true);
		labelWriter.writePixelOffsetProperty(0.0, -25.0);

		labelWriter.close();
	}

	@Override
	public void handleStep(SpacecraftState spacecraftState, boolean isLast) {
		createCzmlTimeTaggedPosition(finishedListener, frame, spacecraftState, isLast);
	}

	/**
	 * 
	 * @param finishedListener
	 * @param frame
	 * @param spacecraftState
	 * @param isLast
	 */
	private void createCzmlTimeTaggedPosition(final PropagationFinishedListener finishedListener, final Frame frame, SpacecraftState spacecraftState, boolean isLast) {
		PVCoordinates coords;
		try {
			coords = spacecraftState.getPVCoordinates(frame);
		}
		catch (final OrekitException e) {
			LOG.warn(SPACE_DYNAMICS, "Error getting position/velocity pair from spacecraft state." + e.getMessage() + ". Sipping propagation frame.");
			return;
		}
		DateTime isoDate;
		try {
			isoDate = new DateTime(spacecraftState.getDate().toDate(TimeScalesFactory.getUTC()));
		}
		catch (final OrekitException e) {
			LOG.warn(SPACE_DYNAMICS, "Orekit exception when getting UTC timescale. Indicates orkit config zip is corrupt or unreadable. Skipping propagation step.");
			return;
		}
		final Vector3D pos = coords.getPosition();

		addPositionPair(isoDate, pos);

		if (isLast) {
			// Add the final position pair and write out all the complete position property including sub-properties.
			writePositionProperty(dates, cartesians, frame);

			spacecraftPacket.close();
			czmlOutStream.writeEndObject();
			System.out.println(stringWriter.toString());

			notifyFinishedListeners(finishedListener);
		}
	}

	/**
	 * Writes the position property to the {@link #spacecraftPacket} using it's position writer.
	 * 
	 * @param dates
	 * @param cartesians
	 * @param frame
	 */
	private void writePositionProperty(List<JulianDate> dates, List<Cartesian> cartesians, final Frame frame) {
		final PositionCesiumWriter positionWriter = spacecraftPacket.getPositionWriter();
		positionWriter.open(czmlOutStream);

		positionWriter.writeInterpolationAlgorithm(CesiumInterpolationAlgorithm.LAGRANGE);
		positionWriter.writeInterpolationDegree(INTERPOLATION_DEGREE);
		if (frame.isPseudoInertial()) {
			positionWriter.writeReferenceFrame("INERTIAL");
		}
		else {
			positionWriter.writeReferenceFrame("FIXED");
		}
		positionWriter.writeCartesian(dates, cartesians);

		positionWriter.close();
	}

	/**
	 * Creates the availability property and writes it to the {@link #spacecraftPacket}
	 * 
	 * @param spacecraftState
	 * @param date
	 * @throws PropagationException
	 */
	private void writeAvailability(SpacecraftState spacecraftState, AbsoluteDate date) throws PropagationException {
		try {
			final JulianDate startAvailabilty = new JulianDate(new DateTime(spacecraftState.getDate().toDate(TimeScalesFactory.getUTC())));
			final JulianDate endAvailability = new JulianDate(new DateTime(date.toDate(TimeScalesFactory.getUTC())));
			spacecraftPacket.writeAvailability(startAvailabilty, endAvailability);
		}
		catch (final OrekitException e) {
			LOG.warn(SPACE_DYNAMICS, "Orekit exception when getting UTC timescale. Indicates orkit config zip is corrupt or unreadable. Aborting CZML generation.");
			throw new PropagationException(e);
		}
	}

	/**
	 * Adds a position and it's associated date to the necessary lists.
	 * 
	 * @param isoDate
	 *            Date for the position
	 * @param pos
	 *            3D Vector of the position data.
	 */
	private void addPositionPair(DateTime isoDate, Vector3D pos) {
		cartesians.add(new Cartesian(pos.getX(), pos.getY(), pos.getZ()));
		dates.add(new JulianDate(new DateTime(isoDate)));
	}

	/**
	 * Notify all {@link PropagationFinishedListener}s that the Propagation and CZML generation has finished.
	 * 
	 * @param finishedListener
	 */
	private final void notifyFinishedListeners(final PropagationFinishedListener finishedListener) {
		if (finishedListener != null) {
			finishedListener.finished();
		}
	}

	private static String createBase64EncodedStringFromURL(URL url) throws IOException {
		final InputStream inputStream = url.openStream();
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final byte[] buffer = new byte[4096];

		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		outputStream.close();
		inputStream.close();

		final byte[] bytes = outputStream.toByteArray();
		return "data:image/png;base64," + DatatypeConverter.printBase64Binary(bytes);
	}
}
