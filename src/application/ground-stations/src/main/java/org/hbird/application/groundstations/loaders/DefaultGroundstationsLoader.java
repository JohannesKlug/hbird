package org.hbird.application.groundstations.loaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hbird.application.groundstations.Groundstation;
import org.hbird.application.groundstations.KmlGroundstationFactory;
import org.hbird.application.groundstations.exceptions.InvalidGroundstationKml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class DefaultGroundstationsLoader {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultGroundstationsLoader.class);

	private static final Marker GROUND_STATIONS = MarkerFactory.getMarker("GROUND STATIONS");

	private static final String HBIRD_DEFAULT_GROUNDSTATIONS_KML = "Hbird-default-groundstations.kml";

	private static List<Groundstation> loadAllGroundStationsFrom(InputStream stream) throws InvalidGroundstationKml {
		Kml kml = Kml.unmarshal(stream);
		Feature feature = kml.getFeature();
		return processFeature(feature);
	}

	public static List<Groundstation> loadAllGroundStationsFrom(String filePath) throws InvalidGroundstationKml {
		if (StringUtils.isBlank(filePath)) {
			throw new IllegalArgumentException("Groundstations KML file direcotry path is null or blank.");
		}

		File gsFile = new File(filePath);
		Kml kml = Kml.unmarshal(gsFile);
		Feature feature = kml.getFeature();
		return processFeature(feature);
	}

	private static List<Groundstation> processFeature(Feature feature) throws InvalidGroundstationKml {
		// NASTY Java KML library API.
		if (feature instanceof Document) {
			return processDocument((Document) feature);
		}
		else if (feature instanceof Placemark) {
			return new ArrayList<>(Arrays.asList(KmlGroundstationFactory.createGroundstationFrom((Placemark) feature)));
		}
		LOG.error(GROUND_STATIONS, "Expected the first feature in the KML file to be a Document or Placemark. Cannot process file.");
		throw new InvalidGroundstationKml();
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	private static List<Groundstation> processDocument(Document doc) {
		List<Groundstation> stations = new ArrayList<>();
		// NASTY Java KML library API.
		List<Feature> folderFeatures = doc.getFeature();
		for (Feature folderFeature : folderFeatures) {
			if (folderFeature instanceof Folder) {
				Folder folder = (Folder) folderFeature;
				List<Feature> placemarkFeatures = folder.getFeature();
				for (Feature placemarkFeature : placemarkFeatures) {
					stations.add(KmlGroundstationFactory.createGroundstationFrom((Placemark) placemarkFeature));
				}
			}
		}

		return stations;
	}

	public static List<Groundstation> loadDefaultGroundstations() throws InvalidGroundstationKml {
		InputStream stream = DefaultGroundstationsLoader.class.getResourceAsStream(HBIRD_DEFAULT_GROUNDSTATIONS_KML);
		List<Groundstation> stations = loadAllGroundStationsFrom(stream);
		try {
			stream.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stations;
	}
}
