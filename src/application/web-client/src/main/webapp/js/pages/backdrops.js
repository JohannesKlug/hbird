var rootUrl = location.protocol + "//" + window.location.hostname + ":" + location.port;
var rootHbirdwebUrl = rootUrl + "/hbirdweb/";

var imagesDiv;

/** images cycled on menu click - order is the same as the menu list */
var imgSunRise = $('<img src=' + rootHbirdwebUrl + 'images/earth-sunrise-space.jpg>');
var imgEarth = $('<img src=' + rootHbirdwebUrl + 'images/earth-backdrop.jpg>');
var imgGsSunset = $('<img src=' + rootHbirdwebUrl + 'images/ground-station-sunset.jpg>');
var imgShuttleSil = $('<img src=' + rootHbirdwebUrl + 'images/shuttle-silhouette.jpg>');

var currentImgIndex;
var imageTextPositions = ['right', 'left', 'left', 'right'];


/**
 * On page ready do the following.
 */
jQuery(document).ready(function() {
	setupBackdrops();
	changeBackdrop(-1);
});

/**
 * Must have the hbird-base.css file for the backdrop styling.
 */
function setupBackdrops() {
	imagesDiv = $('<div id=backdrop>');
	imagesDiv.append(imgSunRise);
	imagesDiv.append(imgEarth);
	imagesDiv.append(imgGsSunset);
	imagesDiv.append(imgShuttleSil);
	$('body').prepend(imagesDiv);
}


function changeBackdrop(index) {
	// Change backdrop
	console.log("Changing backdrop to index " + index);
	$('img', imagesDiv).removeClass("opaque");
	if(index >= 0) {
		currentImgIndex = index;
		$('img', imagesDiv).eq(index).addClass("opaque");
	}
	else {
		currentImgIndex = Math.floor(Math.random()*4);
		$('img', imagesDiv).eq(currentImgIndex).addClass("opaque");
	}
}

function fadeoutBackdrop() {
	$('img', imagesDiv).removeClass("opaque");
}

function getBackdropTextPosition() {
	return imageTextPositions[currentImgIndex];
}
