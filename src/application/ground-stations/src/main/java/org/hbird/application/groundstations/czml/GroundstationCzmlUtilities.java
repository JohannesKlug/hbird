package org.hbird.application.groundstations.czml;

import cesiumlanguagewriter.BillboardCesiumWriter;
import cesiumlanguagewriter.Cartographic;
import cesiumlanguagewriter.CesiumOutputStream;
import cesiumlanguagewriter.CesiumResourceBehavior;
import cesiumlanguagewriter.CesiumStreamWriter;
import cesiumlanguagewriter.LabelCesiumWriter;
import cesiumlanguagewriter.PacketCesiumWriter;
import cesiumlanguagewriter.PositionCesiumWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.hbird.application.groundstations.Groundstation;
import org.hbird.core.commons.util.Base64Utils;

/**
 * 
 * This class provides CZML functions for creating CZML output for {@link Groundstation}s.
 * 
 * @author Mark Doyle
 * 
 */
public class GroundstationCzmlUtilities {

	private static final CesiumStreamWriter czmlStreamWriter = new CesiumStreamWriter();

	private static final String GS_PNG_IMAGE_PATH = "/images/satellite_ground_32.png";

	/**
	 * Generates a CZML String representing the given list of Groundstations.
	 * 
	 * @param stations
	 *            the ground stations to create the CZML for.
	 * @return the CZML String.
	 */
	public static String generateCzmlFromGroundstations(List<Groundstation> stations) {
		StringWriter stringWriter = new StringWriter();
		CesiumOutputStream czmlOutStream = new CesiumOutputStream(stringWriter);
		czmlOutStream.setPrettyFormatting(true);

		czmlOutStream.writeStartSequence();
		for (Groundstation gs : stations) {
			createStationPacket(gs, czmlOutStream);
		}
		czmlOutStream.writeEndSequence();

		return stringWriter.toString();
	}

	private static void createStationPacket(Groundstation gs, CesiumOutputStream czmlOutStream) {
		PacketCesiumWriter gsPacket = czmlStreamWriter.openPacket(czmlOutStream);
		gsPacket.writeId(gs.getName());
		writeLabel(gsPacket, czmlOutStream, gs);
		writeGsPosition(gsPacket, czmlOutStream, gs);
		writeBillboard(gsPacket, czmlOutStream);
		gsPacket.close();
	}

	private static void writeLabel(PacketCesiumWriter gsPacket, CesiumOutputStream czmlOutStream, Groundstation gs) {
		LabelCesiumWriter labelWriter = gsPacket.getLabelWriter();
		labelWriter.open(czmlOutStream);
		labelWriter.writeTextProperty(gs.getName());
		labelWriter.writeScaleProperty(0.4);
		labelWriter.writePixelOffsetProperty(0.0, -25.0);
		labelWriter.close();

	}

	private static void writeGsPosition(PacketCesiumWriter gsPacket, CesiumOutputStream czmlOutStream, Groundstation gs) {
		PositionCesiumWriter positionWriter = gsPacket.getPositionWriter();
		positionWriter.open(czmlOutStream);
		positionWriter.writeCartographicDegrees(new Cartographic(gs.getLongitudeDegrees(), gs.getLatitudeDegrees(), gs.getAlititude()));
		positionWriter.close();
	}

	private static void writeBillboard(PacketCesiumWriter gsPacket, CesiumOutputStream czmlOutStream) {
		final BillboardCesiumWriter billboardWriter = gsPacket.getBillboardWriter();
		billboardWriter.open(czmlOutStream);

		String gsPng = null;
		try {
			gsPng = Base64Utils.createBase64EncodedStringFromURL(GroundstationCzmlUtilities.class.getResource(GS_PNG_IMAGE_PATH));
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		billboardWriter.writeImageProperty(gsPng, CesiumResourceBehavior.EMBED);

		billboardWriter.close();
	}
}
