requirejs.config({
    baseUrl : "../../js",
    paths : {
    	"Cesium" : "../Cesium/Cesium",
    	"jquery.pnotify.min" : "pines/jquery.pnotify.min",
    	"rootMenu" : "hbird/rootMenu"
    },
    shim : {
    	"jquery.pnotify.min" : {
    		deps : ["jquery"]
    	},
    	"jquery.gracefulWebSocket" : {
    		deps : ["jquery"]
    	},
    	"Cesium" : {
    		// This version of Cesium has no AMD support so we must export the Global.
    		exports: "Cesium"
    	}
    }
});

require([
"jquery",
"Cesium",
"json2",
"jquery.gracefulWebSocket",
"jquery.pnotify.min",
"rootMenu"],
function($, Cesium, json2, graceWebSocket, pnotify, hbirdMenu) {

	// The root URL for the RESTful services
	var host = window.location.hostname;
	var url = "/hbird/halcyon/";
	var rootURL = location.protocol + "//" + host + ":" + location.port + url;

	var cesiumViewer = null;
	var liveTmWebsocket = null;
	var satInput = null;


	/**
	 * Add the add satellite input field to the controls section of the globe page. 
	 */
	function addSatelliteSelection() {
		var form = $("<form id=satForm />").submit(function() {
			loadCzml(satSelectfield.val());
			console.log("Returning false");
			return false;
		});
		
		satInput = $("<input type=search placeholder='search for a satellite' />");

		hbirdMenu.addMenuItem(satInput, function() {
			this.loadCzml(satSelectfield.val());
			console.log("Returning false");
			return false;
		});
	}

	/**
	 * Requests an orbit propagation (as CZML) for the given satellite name via the
	 * Halcyon web service and loads the result into the globe.
	 * 
	 * @param String
	 *            satName the name of the satellite to add to the globe.
	 */
	function loadCzml(satName) {
		var url = rootURL + "navigation/tle/propagate/czml/" + satName;
		$.getJSON(url, null, function(data, textStatus, jqXHR) {
			var czml = jQuery.parseJSON(jqXHR.responseText);

			// Add dynamic CZML data source.
			var czmlDataSource = new Cesium.CzmlDataSource();
			czmlDataSource.load(czml, 'Propagation for ' + satName);
			cesiumViewer.dataSources.add(czmlDataSource);

		}).fail(function() {
			console.log("Error loading czml propagation for satellite " + satName);
		});
	}

	/**
	 * Creates the Cesium viewer and applies default hbird settings.
	 */
	function setupGlobe() {
		cesiumViewer = new Cesium.Viewer('cesiumContainer');

		cesiumViewer.centralBody.terrainProvider = new Cesium.CesiumTerrainProvider({
			url : 'http://cesium.agi.com/smallterrain'
		});

		cesiumViewer.centralBody.enableLighting = true;

		// For dynamic object camera lock on
		cesiumViewer.extend(Cesium.viewerDynamicObjectMixin);
	}

	/**
	 * Function called when a live telemetry parameter is received from the
	 * websocket.
	 * 
	 * @param parameter
	 *            the new tm paramter
	 */
	function parameterReceived(parameter) {

	}

	/**
	 * Sets up the web socket and it's callbacks.
	 */
	function setupWebsocket() {
		var wsProtocol;

		if (location.protocol == "http:") {
			wsProtocol = "ws:";
		}
		else {
			wsProtocol = "wss:";
		}

		liveTmWebsocket = $.gracefulWebSocket(wsProtocol + "//" + host + ":" + location.port + url + "websocket");

		liveTmWebsocket.onopen = function() {
			$.pnotify({
				title : "System message",
				text : "Websocket connection established.",
				type : "info",
				icon : "'picon picon-network-wireless'"
			});
		};

		liveTmWebsocket.onerror = function() {
			$.pnotify({
				title : "System message",
				text : "Websocket connection failed. Cannot receive live telemetry.",
				type : "error",
				icon : "'picon picon-network-wireless'"
			});
		};

		// When we receive a message from the websocket first check if there are any
		// widgets on the dashboard,
		// if so, parse the message into a parameter object and call our handler.
		liveTmWebsocket.onmessage = function(event) {
			var message = $.parseJSON(event.data);
			if (message.id === "LIVE_TM") {
				if (Object.keys(hidgetMonitorMap).length > 0) {
					parameterReceived(message.content);
				}
			}
		};
	}

	setupWebsocket();
	setupGlobe();
	addSatelliteSelection();
	hbirdMenu.startMenu();	
		
});


