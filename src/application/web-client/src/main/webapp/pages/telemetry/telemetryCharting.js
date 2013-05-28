// The root URL for the RESTful services
var host = window.location.hostname;
var url = "/hbird/halcyon/";
var rootURL = location.protocol + "//" + host + ":" + location.port + url;

var live = true;

var parametersPlotted = new Array();
var seriesData = [];
var chartData = new Array();
//		{
//	label : "Foo",
//	data : [ [ 10, 1 ], [ 17, -14 ], [ 30, 5 ] ]
//});
// for css styling ^
var liveTmChart;

var rollChartIntervalId;
var rollingChart;

var maxDataSeriesSize = 400;

var omniSearchInput;


// -----------------

/**
 * On page ready do the following.
 */
jQuery(document).ready(function() {
//	$.pnotify.defaults.styling = "jqueryui";
	setupJqueryDefaults();
	setupVariables();
	setupChosen();
	setupOmniSearch();
	setupWebsocket();
	setupChartOptionsForm();
	setupChart();
	setupControls();
});

function setupControls() {
	setupTimeRangeSlider();
	setupArchiveSubmit();

	$("#goLiveButton").click(function(){
		var clear = liveTmChart.getData();
		clear = [];
		for(name in seriesData) {
			console.log("Switching to live; clearing data in " + name);
			seriesData[name] = [];
		}
		liveTmChart.setData(clear);
    	liveTmChart.setupGrid();
    	liveTmChart.draw();
		live = true;
	});
}

function setupArchiveSubmit() {
	$("#dateRangeOptionsForm").submit(function() {
		live = false;
		console.log("Plotting....");
		var dateRange = $("#dateRangeSlider").dateRangeSlider("values");

		var queryRequest = new Object();
		queryRequest.startTime = dateRange.min.getTime();
		queryRequest.endTime = dateRange.max.getTime();
		queryRequest.parameterQualifiedName = parametersPlotted;
		
		$.post(rootURL + "tm/parameterarchive/hbirdquery", JSON.stringify(queryRequest), function(results) {
			updateChart(results);
		},
		"json");
	});
}

/**
 * Used for archive chart updates. Clears the existing data.
 * FIXME Horribly inefficient. Has to loop through the entire data set to create 
 * the newData with flot metadata i.e. label and data.
 * 
 * @param parameters
 */
function updateChart(parameters) {
	var curProg = 1;
    var progress;
    var crement = 100 / (parameters.length);


	// Make a loader.
	var loader = $.pnotify({
	    title: "Plotting graph...",
	    text: "<div class=\"progress_bar\" />",
	    icon: 'picon picon-throbber',
	    hide: false,
	    closer: false,
	    sticker: false,
	    history: false,
	    before_open: function(pnotify) {
	        progress = pnotify.find("div.progress_bar");
	        progress.progressbar({
	            value: curProg
	        });
	        
	        /**/
	        var newData = [];
	    	
	    	// We must clear out the old data otherwise we'll re-plot it with 
	    	// the new data.
	    	var series;
	    	$.each(parametersPlotted, function(i) {
	    		series = createDataSeries(parametersPlotted[i]);
	    		series.length = 0;
	    	});
	    	
	    	// Push the new data into the series.
	    	$.each(parameters, function(i) {
	    		var series = getSeriesData(parameters[i].qualifiedName);
	    		series.push([parameters[i].receivedTime, parameters[i].value ]);
	    		curProg += crement;
	    	});

	    	// Add Flot metadata and wrap in new data array.
	    	$.each(parametersPlotted, function(i) {
	    		newData.push({
	    			"label" : parametersPlotted[i],
	    			"data" : getSeriesData(parametersPlotted[i])
	    		});
	    		
	    	});
	    	
	    	setNumParametersChartInfo(parameters.length);
	    	$("#chartInfo").addClass("visible");
	    	
	    	liveTmChart.setData(newData);
	    	liveTmChart.setupGrid();
	    	liveTmChart.draw();
	    	/**/
	    	
	    }
	});

	loader.pnotify_remove();
	
}

function setNumParametersChartInfo(num) {
//	$(".numParametersPlotted").remove();
	$("#numParametersPlotted").text(num);
}


function setupJqueryDefaults() {
	// Set json as default content-type for ajax. Since we are only sending JSON it means 
	// we can use the shorthand post. 
	$.ajaxSetup({
	    contentType: "application/json; charset=UTF-8"
	});
}

function setupTimeRangeSlider() {
	var future = $.post(url + "tm/parameterarchive/queryMin", "receivedTime", function(data, textStatus, jqXHR) {
		console.log("success");
		
		var maxRange = new Object();
		maxRange.days = $("#maxRangeInput").val();
		
		var timestamp = jQuery.parseJSON(jqXHR.responseText).receivedTime;
		var defaultUpper = new Date(timestamp);
		defaultUpper.setDate(defaultUpper.getDate() + maxRange.days);
		
		$("#dateRangeSlider").dateRangeSlider({
			bounds: { 
				min: timestamp, 
				max: new Date().valueOf()
			},
			defaultValues: {
				min: timestamp,
				max: defaultUpper.valueOf()
			},
			range: {
				min: false,
				max: maxRange
			}
		});
	}, 
	"json");
	
	future.error(function() {
		$("#dateRangeSlider").dateRangeSlider();
		$("#dateRangeSlider").removeClass("visible");
	});
	
	$("#maxRangeInput").change(function() {
		var newMax = $(this).val();
		if(newMax == false) {
			newMax = false;			
		}
		else {
			newMax = {"days" : $(this).val()};			
		}
		$("#dateRangeSlider").dateRangeSlider("option", "range", {
			min:false,
			max: newMax
		});
	});
	
}

function setupVariables() {
	omniSearchInput = $("#parameterAddInput");
}

function setupOmniSearch() {
	// Populate auto-complete datalist over ajax request to publisher
	$("#parameterAddInput").on("input", function(e) {
		var val = $(this).val();
		if(val < 1) {
			return;
		}
		var url = rootURL + "tm/parameters/";
		$.getJSON(url + val, null, function(data, textStatus, jqXHR) {
			parameters = jQuery.parseJSON(jqXHR.responseText);
			var parameterList = $("#parameterList");
			parameterList.empty();
			$.each(parameters, function(i) {
				parameterList.append(new Option(parameters[i].name, parameters[i].qualifiedName, false, false));
			});
		});
	});
	
	
	// On submit
	$("#parameterAddForm").submit(function() {
		var input = omniSearchInput.val();
		var option = $("#parameterList").children();
		var found = false;
		$.each(option, function(i) {
			console.log("input = " + input + ". child val = " + $(option[i]).val());
			if(input === $(option[i]).val()) {
				createDataSeries(input);
				found = true;
				$("legend").addClass("visible");
				return false; // this is the same as a break in the jquery each function
			}
		});
		if(!found) {
			$.pnotify({
			    title: "Search failure",
			    text: "Could not find a parameter called " + input,
			    type: "error",
			    icon: "picon picon-page-zoom"
			});
		}
		return false;
	});
}

function setupChart() {
	var options = {
		xaxis : {
			mode : "time",
			color : "#FFF"
		},
		yaxis : {
			color : "#DDD"
		},
		legend : {
			backgroundColor : "#999",
			container : $("#legend")
		}
	};

	liveTmChart = $.plot($("#liveTmChart"), chartData, options);

	// setup overview
	var overview = $.plot($("#overview"), chartData, {
		legend : {
			show : true,
		},
		series : {
			lines : {
				show : true,
				lineWidth : 1
			},
			shadowSize : 0
		},
		grid : {
			color : "#999"
		},
		selection : {
			mode : "xy"
		}
	});

	$("#liveTmChart").bind("plotselected", function(event, ranges) {
		if(live) {
			return;
		}
		// clamp the zooming to prevent eternal zoom
		if (ranges.xaxis.to - ranges.xaxis.from < 0.00001) {
			ranges.xaxis.to = ranges.xaxis.from + 0.00001;
			}
		if (ranges.yaxis.to - ranges.yaxis.from < 0.00001) {
			ranges.yaxis.to = ranges.yaxis.from + 0.00001;
		}

		// do the zooming
		liveTmChart = $.plot($("#liveTmChart"), getData(ranges.xaxis.from, ranges.xaxis.to), 
			$.extend(true, {}, 
				options, {
				xaxis : {
					min : ranges.xaxis.from,
					max : ranges.xaxis.to
				},
				yaxis : {
					min : ranges.yaxis.from,
					max : ranges.yaxis.to
				}
			})
		);

		// don't fire event on the overview to prevent eternal loop
		overview.setSelection(ranges, true);
	});

	$("#overview").bind("plotselected", function(event, ranges) {
		if(live) {
			return;
		}
		liveTmChart.setSelection(ranges);
	});
}

function setupChartOptionsForm() {
	$("#maxPointsInput").val(maxDataSeriesSize);

	$("#chartOptionsForm").submit(function(event) {
		maxDataSeriesSize = $("#maxPointsInput").val();
	});
}

function setupChosen() {
	$(".chzn-select").chosen(); // Activate chosen plugin
	$(".chzn-select-deselect").chosen({
		allow_single_deselect : true
	});
	$("#parametersList").change(parameterSelectionChanged);
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

	var liveTmWebsocket = $.gracefulWebSocket(wsProtocol + "//" + host + ":"
			+ location.port + url + "tmsock");

	liveTmWebsocket.onopen = function() {
		console.log("Websocket connection established.");
	};

	liveTmWebsocket.onmessage = function(event) {
		if (live) {
			plotParameter($.parseJSON(event.data));
		}
	};
}

/**
 * Calls the Halcyon restful web service to gather the TM parameters list then
 * updated the TM list on the page.
 */
function retreiveTmList() {
	var jqxhr = $.getJSON(rootURL + "tm/parameters");

	jqxhr.done(function(parsedResponse, statusText, jqXhr) {
		updateTelemetryList(jQuery.parseJSON(jqXhr.responseText));
	});
}

/**
 * Accepts an array of parameters (Hbird commons tmtc Parameter) and adds them
 * to the parameter selection form as options.
 * 
 * @param param
 */
function updateTelemetryList(param) {
	$("#telemetryList").empty();
	$("#parametersList").trigger("liszt:updated");

	$.each(param, function(i) {
		$("#parametersList").append(new Option(param[i].name, param[i].qualifiedName,false, false));
		$("#datalist1").append(new Option(param[i].qualifiedName, param[i].name, false, false));
	});
	
}

/**
 * [ 
 * 	{ label: "Foo", data: [ [10, 1], [17, -14], [30, 5] ] }, 
 * 	{ label: "Bar", data: [ [11, 13], [19, 11], [30, -7] ] } 
 * ]
 * 
 * @param parameter
 *            JS object of Parameter object.
 */
function plotParameter(parameter) {
	// Get the parameters series data and append the new data to it. If the
	// series does not exist return and do nothing.
	var parameterSeries = getSeriesData(parameter.qualifiedName);
	if (typeof parameterSeries === "undefined") {
		return;
	}

	// Push the new data into the parameter series array and remove the oldest
	// entry if the array
	// has grown too large.
	var size = parameterSeries.push([ parameter.receivedTime, parameter.value ]);
	if (size >= maxDataSeriesSize) {
		parameterSeries.shift();
	}

	// Create the new data for the chart by added all seriesData to a new array
	// and setting on the plot.
	// FIXME Crap array copying code. Don't even need to do it! Idiot.
	var newData = [];
	var totalPlottedPoints = 0;
	for(var i in seriesData) {
		newData.push({
			"label" : i,
			"data" : seriesData[i]
		});
		totalPlottedPoints += seriesData[i].length;
	}
	liveTmChart.setData(newData);

	$("#chartInfo").addClass("visible");
	setNumParametersChartInfo(totalPlottedPoints);

	// Redraw
	liveTmChart.setupGrid();
	liveTmChart.draw();
}

/**
 * Gets the series from the seriesData Map given the qualified name.
 * 
 * @param name
 * @returns
 */
function getSeriesData(name) {
	if (name in seriesData) {
		return seriesData[name];
	}
}

/**
 * Called when parameter combo-box selection has changed.
 */
function parameterSelectionChanged() {
	$("select option:selected").each(function() {
		createDataSeries($(this).val());
	});
}

/**
 * series data format as follows: { qualifiedName:[], qualifiedName:[] }
 * 
 * FIXME Will not remove existing plot lines that have been deselected.
 * 
 */
function createDataSeries(name) {
	if(!$("#chartArea").hasClass("visible")) {
		$("#chartArea").addClass("visible");
	}
	if (name in seriesData) {
		return seriesData[name];
	}

	var data = [];
	seriesData[name] = data;
	parametersPlotted.push(name);
	$.pnotify({
	    title: "Added parameter",
	    text: name,
	    type: "success"
	});
	return seriesData[name];
}
