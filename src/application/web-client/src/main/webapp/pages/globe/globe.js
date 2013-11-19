// Test
var czml = [
	{
		"id" : "Strand-1",
		"position": { 
			"cartesian": [8.63517, 49.87147, 20.0]
		},
        "point": {
            "color": { 
            	"rgba": [255, 0, 0, 255] 
            },
            "pixelSize" : 5
        }
	}
];

var cesiumWidget;

/**
 * On page ready do the following.
 */
jQuery(document).ready(function() {
	setupGlobe();
});

function setupGlobe() {
	cesiumWidget = new Cesium.Viewer('cesiumContainer');
	var layers = cesiumWidget.centralBody.getImageryLayers();
	
	var czmlDataSource = new Cesium.CzmlDataSource();
    czmlDataSource.load(czml, 'Test CZML');
    cesiumWidget.dataSources.add(czmlDataSource);
}