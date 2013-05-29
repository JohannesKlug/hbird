/**
 * Hidget = hbird widget 
 */

var host = window.location.hostname;
var url = "/hbird/halcyon/";

/** The root URL for the RESTful services */
var rootURL = location.protocol + "//" + host + ":" + location.port + url;
var halcyonUrl = location.protocol + "//" + host + ":" + location.port + "/hbird/halcyon/";

var liveTmWebsocket;

var gridster;

var colours = ["metroPurple",
               "metroMagenta",
               "metroTeal",
               "metroLime",
               "metroPink",
               "metroOrange",
               "metroBlue",
               "metroRed",
               "metroGreen"];

/** Map of references to all widgets to save using DOM selection. Keyed on id. */
var widgets = [];

/** Map of widget ids keyed on the parameter qualified name they are monitoring */ 
var hidgetMonitorMap = {};

var widget = '<li class="widget monitor">' + 
			 	'<span class="titleArea"><h2>Parameter monitor</h2></span>' +
			 	'<div class=content id=defaultContent>' +
			 		'<p>Configure the widget using the settings button.</p>' + 
				'</div>' + 
			 '</li>';

var plotWidgetHtml = '<li class="widget plot">' + 
				 	'<span class="titleArea"><h2>Parameter plot</h2></span>' +
				 	'<div class="content" id=defaultContent>' +
				 		'<p id="defaultContent">Configure the widget using the settings button.</p>' +
				 	'</div>' +
				  '</li>';

/** Plot options */
var options;

/** All the plots instantiated on the dashboard */
var plots = {};

/** Map of all the data series arrays for every plot widget */
var plotSeriesMap = {};

/** Max number of points to plot */
var maxSeriesPoints = 75;

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
		var monitorWidget = gridster.add_widget(widget, 2, 1);

		// Grab a unique ID and increment the counter. We use this to operate on the hidget, 
		// e.g., pop up submenus etc.
		var currentId = hidgetId++;
		monitorWidget.attr("id",  "hidget" + currentId);

		// Create the internal markup for the hidget.
		var searchForm = createMonitorSearchForm(currentId);
		createWidgetCloseButton(currentId).appendTo($(monitorWidget).children(".titleArea"));
		createSettingsButton(currentId).appendTo($(monitorWidget).children(".titleArea"));
		searchForm.appendTo(monitorWidget);

		// Colour the hidget.
		monitorWidget.addClass(grabSomeColour());

		// Track the hidget in a map for quick lookup
		widgets[currentId] = monitorWidget;
	});
	
	$("#addParameterPlot").click(function(){
		// Grab a unique ID and increment the counter. We use this to operate on the widget, 
		// e.g., pop up submenus etc.
		var currentId = hidgetId++;
		
		// Add the hbird plot widget to the grid
		var plotWidget = gridster.add_widget(plotWidgetHtml, 4, 2);

		plotWidget.attr("id",  "hidget" + currentId);

		// Create the internal markup for the hidget.
		var searchForm = createMonitorSearchForm(currentId);
		createWidgetCloseButton(currentId).appendTo($(plotWidget).children(".titleArea"));
		createSettingsButton(currentId).appendTo($(plotWidget).children(".titleArea"));
		searchForm.appendTo(plotWidget);

		// Colour the hidget.
		plotWidget.addClass(grabSomeColour());

		// Track the hidget in a map for quick lookup
		widgets[currentId] = plotWidget;
	});
}


/** Returns a random colour name from the colours array. Names are linked to the CSS style. */
function grabSomeColour() {
	return colours[Math.floor((Math.random() * colours.length-1) + 1)];
}

function createWidgetCloseButton(id) {
	var butt = $("<button type=\"button\"/>").attr("id", "closeButton" + id);
	butt.button({
		icons: { primary : "ui-icon-close"},
		text : false
	}).click(function() {
		// Remove any listener "registrations" for this widget id...
		var widget = widgets[id];
		for(var key in hidgetMonitorMap) {
			var updatedListenerList = [];
			updatedListenerList = $.grep(hidgetMonitorMap[key], function(widgetId, index) {
				console.log("DEBUG - id: " + id + " widgetId: " + widgetId);
				if(id === widgetId) {
					return false;
				}
				else {
					return true;
				}
			});
			hidgetMonitorMap[key] = updatedListenerList;
		}
		// Remove widget from the grid.
		gridster.remove_widget(widget);
	});
	return butt;
} 

/**
 * Creates a settings button for a specific widget.
 * 
 * @param id
 * @returns
 */
function createSettingsButton(id) {
	var button = $("<button type=\"button\">Settings</button>").attr("id", "hidgetSettingsButton" + id);
	button.button({
		icons: { primary: "ui-icon-gear" },
	    text : false
	}).click(function() {
		toggleWidgetContent(id);
		$(widgets[id]).children("#searchSection" + id).toggleClass("removed");
	});
	return button;
}

/**
 * Set up the gridster plugin.
 */
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


/**
 * Create a monitor search for for a specific widget id. This is used to search the parameter list.
 * @param id
 * @returns
 */
function createMonitorSearchForm(id) {
	var searchDiv = $("<div id=\"searchSection" + id + "\">").addClass("removed");
	var input = $("<input id=\"parameterSearch\" list=\"parameterList" + id + "\" type=\"search\" results=5 placeholder=\"Search for a parameter name\"" +
						" autofocus=\"autofocus\">")
				.addClass("parameterSearchInput");
	
	// TODO parameterList datalist can be moved to a single location shared by all widgets, right? - Mark
	var form = $("<form id=\"searchForm" + id + "\">")
				.append(input)
				.append('<datalist id=parameterList' + id + '\>');
	
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
				linkWidgetToParameter(parameterQualifiedName, id);
				createWidgetContent(id);
				liveTmWebsocket.send(parameterQualifiedName);
				return false; // this is the same as a break in the jquery each function
			}
		});
		
		if(!found) {
			$.pnotify({
			    title: "Search failure",
			    text: "Could not find a parameter called " + parameterQualifiedName,
			    type: "error",
			    icon: "picon picon-page-zoom"
			});
		}
		return false;
	});
	
	return searchDiv;
}

function linkWidgetToParameter(parameterQualifiedName, widgetId) {
	// First check the listener map for the widget id. If it's present it is already monitoring a parameter 
	// so we should remove that.
	for (var key in hidgetMonitorMap) {
		var updatedListenerList = [];
		updatedListenerList = $.grep(hidgetMonitorMap[key], function(id, index) {
			console.log("DEBUG - id: " + id + " widgetId: " + widgetId);
			if(id === widgetId) {
				// widget already listening to a parameter, let's remove that. If it's a plot we must clear the data too.
				plotSeriesMap[id] = [];
				return false;
			}
			else {
				return true;
			}
		});
		hidgetMonitorMap[key] = updatedListenerList;		
	}
	
	var listeners = []; 
	// If we already have a list of listeners for this parameter, grab it, and append the new listener id, else create a new collection and use that.
	if(!hidgetMonitorMap[parameterQualifiedName]) {
		hidgetMonitorMap[parameterQualifiedName] = listeners;
	}
	else {
		listeners = hidgetMonitorMap[parameterQualifiedName];
	}
	
	listeners.push(widgetId);
}

function createWidgetContent(id) {
	if(widgets[id].hasClass("monitor")) {
		setWidgetContent(id, createMonitorValueDisplay(id));
	}
	else if(widgets[id].hasClass("plot")) {
		setWidgetContent(id, createPlot(id));
		plots[id] = setupChart(id);
	}
	else {
		console.log("Unknown widget type, cannot create content.");
	}
}

function setWidgetContent(id, newContent) {
	var content = $(widgets[id]).children("#defaultContent");
	content.empty();
	content.append(newContent);
	toggleWidgetContent(id);
}

function createMonitorValueDisplay(id) {
	var div = $("<div class=\"valueDisplay\" id=\"valueDisplay" + id + "\">");
	var monitorValue = $("<p class=value id=value" + id + ">--</p>");
	var monitorUnit = $("<p class=unit id=unit" + id + ">unknown unit</p>");
	monitorValue.appendTo(div);
	monitorUnit.appendTo(div);
	
	return div;	
}

function toggleWidgetContent(id) {
	$(widgets[id]).children(".content").toggleClass("removed");
}


function setHidgetTitle(id, title) {
	console.log("Setting title on id " + id + " to " + title);
	$(widgets[id]).children(".titleArea").children("h2").text(title);
}

/**
 * Creates the plot HTML content for the the widget and the plot object.
 * @param id - the plot will be created in the widget with this id.
 * @returns HTML element containing the plot
 */
function createPlot(id) {
	var div = $('<div class=plotDisplay id=plotDisplay' + id + '\>');
	var plot = $('<div class=plot id=plot' + id + '></div>');
	plot.appendTo(div);
	
	return div;
}

/**
 * Sets up the plot options and creates the plot in the widget.
 * @param id - Creates the plot in the widget with this id.
 * @returns the plot object
 */
function setupChart(id) {
	var plot;
	options = {
		series: {
			shadowSize: 0, // Drawing is faster without shadows
			lines: { show: true },
	        points: { show: true }
		},
		xaxis : {
			mode : "time",
			color : "#000"
		},
		yaxis : {
			color : "#000"
		},
	};

	console.log("Creating chart on element #plot" + id);
	plot = $.plot($("#plot" + id), [0, 0], options);

	return plot;
};

function plotParameter(id, parameter) {
	// create new series array, unless it already exists, if so use that.
	var series = []; 
	if(plotSeriesMap[id]) {
		series = plotSeriesMap[id];
	}
	else {
		plotSeriesMap[id] = series;
	}

	// Add the new data and shift array if it's is at the size limit (maxSeriesPoints).
	if(series.push([parameter.receivedTime, parameter.value]) >= maxSeriesPoints) {
		series.shift();
	};
	
	var data = [series];
	// Redraw
	$.plot($("#plot" + id), data, options);
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

	// When we receive a message from the websocket first check if there are any widgets on the dashboard,
	// if so, parse the message into a parameter object and call our handler.
	liveTmWebsocket.onmessage = function(event) {
		if(Object.keys(hidgetMonitorMap).length > 0) {
			parameterReceived($.parseJSON(event.data));
		}
	};
}

/**
 * Triggers updates on all relevant widgets when a new parameter arrives.
 * @param parameter - the new parameter
 */
function parameterReceived(parameter) {
	// If at least one widget is listening to this parameter... 
	if((parameter.qualifiedName in hidgetMonitorMap)) {
		// find every widget listener and update it.
		for (var key in hidgetMonitorMap) {
			if(key === parameter.qualifiedName) {
				$.each(hidgetMonitorMap[key], function(index, widgetId) {
					update(widgetId, parameter);
				});
			}
		}
	}
}

/**
 * Updates the widget with new parameter data
 * 
 * @param id - the widget id
 * @param parameter - the new parameter
 */
function update(id, parameter) {
	var widget = widgets[id];
	if(widget.hasClass("monitor")) {
		widget.find("#value" + id).text(parameter.value);
	}
	else if(widget.hasClass("plot")) {
		plotParameter(id, parameter);
	}
}

