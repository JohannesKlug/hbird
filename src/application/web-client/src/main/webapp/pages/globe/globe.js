// The root URL for the RESTful services
var host = window.location.hostname;
var url = "/hbird/halcyon/";
var rootURL = location.protocol + "//" + host + ":" + location.port + url;

var czml;
var cesiumViewer;
var liveTmWebsocket;

/**
 * On page ready do the following.
 */
jQuery(document).ready(function() {
	setupGlobe();
	loadCzml();
	setupWebsocket();
});

function loadCzml() {
	var url = rootURL + "navigation/tle/propagate/czml/STRAND 1";
	$.getJSON(url, null, function(data, textStatus, jqXHR) {
		czml = jQuery.parseJSON(jqXHR.responseText);

		// Add dynamic CZML data source.
		var czmlDataSource = new Cesium.CzmlDataSource();
	    czmlDataSource.load(czml, 'Test CZML');
	    cesiumViewer.dataSources.add(czmlDataSource);
	    
	}).fail(function() {
		console.log("Error loading czml propagation");
	}).done(function() {
		console.log("Loading attempt complete");
	}); 
}

function setupGlobe() {
	cesiumViewer = new Cesium.Viewer('cesiumContainer');
	
	cesiumViewer.centralBody.terrainProvider = new Cesium.CesiumTerrainProvider({
        url : 'http://cesium.agi.com/smallterrain'
	});
	
	cesiumViewer.centralBody.enableLighting = true;
    
	// For dynamic object camera lock on
	cesiumViewer.extend(Cesium.viewerDynamicObjectMixin);
}

function parameterReceived(parameter) {
	
}

/**
 * Sets up the web socket and it's callbacks.
 */
function setupWebsocket() {
	var wsProtocol;

	if (location.protocol == "http:") {
		wsProtocol = "ws:";
	} else {
		wsProtocol = "wss:";
	}

	liveTmWebsocket = $.gracefulWebSocket(wsProtocol + "//" + host + ":" + location.port + url + "websocket");

	liveTmWebsocket.onopen = function() {
		$.pnotify({
		    title: "System message",
		    text: "Websocket connection established.",
		    type: "info",
		    icon: "'picon picon-network-wireless'"
		});
	};
	
	liveTmWebsocket.onerror = function() {
		$.pnotify({
		    title: "System message",
		    text: "Websocket connection failed. Cannot receive live telemetry.",
		    type: "error",
		    icon: "'picon picon-network-wireless'"
		});
	};

	// When we receive a message from the websocket first check if there are any widgets on the dashboard,
	// if so, parse the message into a parameter object and call our handler.
	liveTmWebsocket.onmessage = function(event) {
		var message = $.parseJSON(event.data);
		if(message.id === "LIVE_TM") {
			if(Object.keys(hidgetMonitorMap).length > 0) {
				parameterReceived(message.content);
			}
		}
	};
}