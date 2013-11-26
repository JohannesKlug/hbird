// The root URL for the RESTful services
var host = window.location.hostname;
var url = "/hbird/halcyon/";
var rootURL = location.protocol + "//" + host + ":" + location.port + url;

// Test
var czml;

var cesiumViewer;

/**
 * On page ready do the following.
 */
jQuery(document).ready(function() {
	setupGlobe();
	loadCzml();
});

function loadCzml() {
	var url = rootURL + "navigation/tle/propagate/czml/STRAND 1";
	$.getJSON(url, null, function(data, textStatus, jqXHR) {
		czml = jQuery.parseJSON(jqXHR.responseText);

		// Add dynamic CZML data source.
		var czmlDataSource = new Cesium.CzmlDataSource();
	    czmlDataSource.load(czml, 'Test CZML');
	    cesiumWidget.dataSources.add(czmlDataSource);
	    
	}).fail(function() {
		console.log("Error loading czml propagation");
	}).done(function() {
		console.log("Loading attempt complete");
	}); 
}

function setupGlobe() {
	cesiumViewer = new Cesium.CesiumWidget('cesiumContainer');
	
	cesiumViewer.centralBody.terrainProvider = new Cesium.CesiumTerrainProvider({
        url : 'http://cesium.agi.com/smallterrain'
	});
	
	cesiumViewer.centralBody.enableLighting = true;
    
	// For dynamic object camera lock on
	cesiumViewer.extend(Cesium.viewerDynamicObjectMixin);
	
	cesiumViewer.scene.moon = new Cesium.Moon();
}