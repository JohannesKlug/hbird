// Hbird Globe.
// @author Mark Doyle

// In case you are wondering, jquery is in the baseUrl dir and is called jquery.js so 
// further config is not required.
requirejs.config({
	baseUrl : "../../js",
	paths : {
		"Cesium" : "Cesium/Cesium",
		"jquery.pnotify.min" : "pines/jquery.pnotify.min",
		"rootMenu" : "hbird/rootMenu"
	},
	shim : {
		"jquery.pnotify.min" : {
			deps : [ "jquery" ]
		},
		"jquery.gracefulWebSocket" : {
			deps : [ "jquery" ]
		},
		"Cesium" : {
			// This version (pre-built minified) of Cesium has no AMD support so
			// we must export the Global.
			exports : "Cesium"
		},
		"jquery.jpanelmenu.min" : {
			deps : [ "jquery" ]
		}
	}
});

require([ "jquery", "Cesium", "json2", "jquery.gracefulWebSocket", "jquery.pnotify.min", "rootMenu" ], 
function($, Cesium, json2, graceWebSocket, pnotify, hbirdMenu) {

	// The root URL for the RESTful services
	var host = window.location.hostname;
	var url = "/hbird/halcyon/";
	var rootURL = location.protocol + "//" + host + ":" + location.port + url;

	var cesiumViewer = null;
	var liveTmWebsocket = null;

	// API Key for bing maps - non-profit use only!
	Cesium.BingMapsApi.defaultKey = 'ApFoUMQn6s0k_GwSC_I1DJS-THYlZQfTZIdDY1JsTNB_poyAKVxOIY8jVhtepTNT';

	/**
	 * Add the add satellite input field to the controls section of the globe
	 * page.
	 */
	function addSatelliteSelection() {
		var satInput = $("<input type=search placeholder='search for a satellite' />");

		var form = $("<form id=satForm />").append(satInput);

		form.on("submit", function(e) {
			e.preventDefault();
			loadCzml($(this).find("input").val());
		});
		hbirdMenu.addMenuItem(form);
	}

	/**
	 * Requests an orbit propagation (as CZML) for the given satellite name via
	 * the Halcyon web service and loads the result into the globe.
	 * 
	 * @param String
	 *            satName the name of the satellite to add to the globe.
	 */
	function loadCzml(satName) {
		var url = rootURL + "navigation/tle/propagate/czml/" + satName;
		$.getJSON(url, null, function(data, textStatus, jqXHR) {
			var czml = jQuery.parseJSON(jqXHR.responseText);

			// Add dynamic CZML data source.
			try {
				var czmlDataSource = new Cesium.CzmlDataSource();
				czmlDataSource.load(czml, 'Propagation for ' + satName);
				cesiumViewer.dataSources.add(czmlDataSource);
			}
			catch (error) {
				$.pnotify({
					title : "Globe error",
					text : "Error loading and adding CZML to globe. " + error,
					type : "error",
					shadow : false
				});
				return;
			}
			$.pnotify({
				title : "Globe info",
				text : "Added " + satName,
				type : "success",
				shadow : false
			});
		}).fail(function(jqxhr, textStatus, error) {
			$.pnotify({
				title : "Globe error",
				text : error,
				type : error,
				shadow : false
			});
			console.log("Error loading czml propagation for satellite " + satName);
			console.log(textStatus);
			console.log(error);
		});
	}

	/**
	 * Creates the Cesium viewer and applies default hbird settings.
	 */
	function setupGlobe() {
		cesiumViewer = new Cesium.Viewer('cesiumContainer');

		cesiumViewer.enableLighting = true;

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

		// FIXME THis is CTLRV from hboard..
		liveTmWebsocket.onmessage = function(event) {
			var message = $.parseJSON(event.data);
			if (message.id === "LIVE_TM") {
				if (Object.keys(hidgetMonitorMap).length > 0) {
					parameterReceived(message.content);
				}
			}
		};
	}

	function loadGroundStations() {
		// Change request to actual ground stations rather than defaults. This would require use of 
		// an MCS configured groundstation service to first get configured stations.
		var url = rootURL + "groundstations/czml/default";
		$.getJSON(url, null, function(data, textStatus, jqXHR) {
			var czml = jQuery.parseJSON(jqXHR.responseText);

			// Add dynamic CZML data source.
			try {
				var czmlDataSource = new Cesium.CzmlDataSource();
				czmlDataSource.load(czml, "'Groundstations");
				cesiumViewer.dataSources.add(czmlDataSource);
			}
			catch (error) {
				$.pnotify({
					title : "Globe error",
					text : "Error loading and adding groundstation CZML to globe. " + error,
					type : "error",
					shadow : false
				});
				return;
			}
		}).fail(function(jqxhr, textStatus, error) {
			$.pnotify({
				title : "Globe error",
				text : error,
				type : error,
				shadow : false
			});
			console.log("Error loading czml for groundstations");
			console.log(textStatus);
			console.log(error);
		});
	}

	setupWebsocket();
	setupGlobe();
	addSatelliteSelection();
	loadGroundStations();
	hbirdMenu.startMenu();

});
