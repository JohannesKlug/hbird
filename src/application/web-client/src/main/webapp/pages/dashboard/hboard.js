/**
 * Hidget = hbird widget 
 */

/** The root URL for the RESTful services */
var host = window.location.hostname;
var url = "/hbird/halcyon/";
var rootURL = location.protocol + "//" + host + ":" + location.port + url;
var halcyonUrl = location.protocol + "//" + host + ":" + location.port + "/hbird/halcyon/";

var liveTmWebsocket;

var gridster;

var colours = ["metroPurple",
               "metroMagenta",
               "metroTeal",
               "metroLime",
               "metroBrown",
               "metroPink",
               "metroOrange",
               "metroBlue",
               "metroRed",
               "metroGreen"];

/** Map of object references to all hidgets to save using dom selection. Keyed on id. */
var hidgets = [];

/** Map of parameter monitor hidget Ids keyed on the parameter qualified name they are monitoring */ 
var hidgetMonitorMap = {};

var defaultHidget = '<li class="hidget">' + 
						'<span class="titleArea"><h2>Parameter monitor</h2></span>' +
						'<div class="content">' +
							'<p id="defaultContent">Configure the widget using the settings button.</p>' + 
						'</div>' + 
					'</li>';


/**
 * Where the magic begins. Ha!
 */
jQuery(document).ready(function() {
	setupJqueryDefaults();
	setupGridster();
	setupControls();
	setupWebsocket();
});


var hidgetId = 0;
function setupControls() {
	$("#addParameterMonitor").click(function(){
		// Add the hbird widget to the grid
		var hidget = gridster.add_widget(defaultHidget, 2, 1);

		// Grab a unique ID and increment the counter. We use this to operate on the hidget, 
		// e.g., pop up submenus etc.
		var currentId = hidgetId++;
		hidget.attr("id",  "hidget" + currentId);

		// Create the internal markup for the hidget.
		var searchForm = createMonitorSearchForm(currentId);
		var button = createSettingsButton(currentId);
		button.appendTo($(hidget).children(".titleArea"));
		searchForm.appendTo(hidget);

		// Colour the hidget.
		var colourIndex = Math.floor((Math.random() * colours.length-1) + 1);
		hidget.addClass(colours[colourIndex]);

		// Track the hidget in a map for quick lookup
		hidgets[currentId] = hidget;
	});
}

function createSettingsButton(id) {
	var button = $("<button type=\"button\">Settings</button>").attr("id", "hidgetSettingsButton" + id);
	button.button({
		icons: {
			primary: "ui-icon-gear"
		},
	    text: false
	}).click(function() {
		toggleDefaultHidgetContent(id);
		$(hidgets[id]).children("#searchSection" + id).toggleClass("removed");
	});
	return button;
}

function setupGridster() {
	$(".gridster ul").gridster({
        widget_margins: [10, 10],
        widget_base_dimensions: [140, 140]
    });
	gridster = $(".gridster ul").
				gridster({widget_margins: [10, 10]}).
				data("gridster");
}


function setupJqueryDefaults() {
	// Set json as default content-type for ajax. Since we are only sending JSON it means 
	// we can use the shorthand post. 
	$.ajaxSetup({
	    contentType: "application/json; charset=UTF-8"
	});
}


function createMonitorSearchForm(id) {
	var searchDiv = $("<div id=\"searchSection" + id + "\">").addClass("removed");
	var input = $("<input id=\"parameterSearch\" list=\"parameterList" + id + "\" type=\"search\" results=5 placeholder=\"Type parameter name\"" +
						" autofocus=\"autofocus\">")
				.addClass("parameterSearchInput");
	
	// TODO parameterList datalist can be moved to a single location shared by all widgets, right? - Mark
	var form = $("<form id=\"searchForm" + id + "\">")
				.append(input)
				.append("<datalist id=\"parameterList" + id + "\" >");
	
	form.appendTo(searchDiv);
	
	// Create AJAX based Restful request to build parameter list based upon input.
	input.on("input", function(e) {
		var val = $(this).val();
		if(val < 1) {
			return;
		}
		var url = halcyonUrl + "tm/parameters/";
		$.getJSON(url + val, null, function(data, textStatus, jqXHR) {
			parameters = jQuery.parseJSON(jqXHR.responseText);
			var parameterList = $("#parameterList" + id);
			parameterList.empty();
			console.log("Received: " + parameters.length);
			$.each(parameters, function(i) {
				parameterList.append(new Option(parameters[i].name, parameters[i].qualifiedName, false, false));
			});
		});
	});
	
	// On submit
	form.submit(function() {
		var parameterQualifiedName = input.val();
		var option = $("#parameterList" + id).children();
		var found = false;
		console.log("Input submitted: " + parameterQualifiedName);
		$.each(option, function(i) {
			console.log("input = " + parameterQualifiedName + ". child val = " + $(option[i]).val());
			if(parameterQualifiedName === $(option[i]).val()) {
				found = true;
				setHidgetTitle(id, $(option[i]).text());
				$("#searchSection" + id).toggleClass("removed");
				hidgetMonitorMap[parameterQualifiedName] = id;
				hidgets[id].append(createMonitorValueDisplay(id));
				liveTmWebsocket.send(parameterQualifiedName);
				return false; // this is the same as a break in the jquery each function
			}
		});
		if(!found) {
			$.pnotify({
			    title: "Search failure",
			    text: "Could not find a parameter called " + inputValue,
			    type: "error",
			    icon: "picon picon-page-zoom"
			});
		}
		return false;
	});
	
	return searchDiv;
}

function createMonitorValueDisplay(id) {
	var div = $("<div class=\"valueDisplay\" id=\"valueDisplay" + id + "\">");
	var monitorValue = $("<p class=value id=value" + id + ">--</p>");
	var monitorUnit = $("<p class=unit id=unit" + id + ">unknown unit</p>");
	monitorValue.appendTo(div);
	monitorUnit.appendTo(div);
	
	return div;	
}

function toggleDefaultHidgetContent(id) {
	$(hidgets[id]).children(".content").children("#defaultContent").toggleClass("removed");
}


function setHidgetTitle(id, title) {
	console.log("Setting title on id " + id + " to " + title);
	$(hidgets[id]).children(".titleArea").children("h2").text(title);
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

	liveTmWebsocket = $.gracefulWebSocket(wsProtocol + "//" + host + ":" + location.port + url + "tmsock");

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

	liveTmWebsocket.onmessage = function(event) {
		// FIXME DO check here so we don't waste time parsing JSON if there are no widgets around.
		parameterReceived($.parseJSON(event.data));
	};
}

function parameterReceived(parameter) {
	if((parameter.qualifiedName in hidgetMonitorMap)) {
		update(hidgetMonitorMap[parameter.qualifiedName], parameter);
	}
}

function update(id, parameter) {
	$(hidgets[id]).find("#value" + id).text(parameter.value);
}

