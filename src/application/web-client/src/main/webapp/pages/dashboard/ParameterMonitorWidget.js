function ParameterMonitorWidget(id) {
	this.id = id;
}

ParameterMonitorWidget.prototype.createContentHtml = function() {
	var div = $("<div class=\"valueDisplay\" id=\"valueDisplay" + this.id + "\">");
	var monitorValue = $("<p class=value id=value" + this.id + ">--</p>");
	var monitorUnit = $("<p class=unit id=unit" + this.id + ">unknown unit</p>");
	monitorValue.appendTo(div);
	monitorUnit.appendTo(div);
	
	return div;	
};