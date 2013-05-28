function PlotWidget(id) {
	this.id = id;
	
}

PlotWidget.prototype.createContentHtml = function() {
    var div = $('<div class="plot" id="plotContainer"' + this.id + '\>');

	return div;
};