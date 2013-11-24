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
	loadCzml();
	setupGlobe();
});

function loadCzml() {
	
}

function setupGlobe() {
	cesiumWidget = new Cesium.Viewer('cesiumContainer');
	
	cesiumWidget.centralBody.terrainProvider = new Cesium.CesiumTerrainProvider({
        url : 'http://cesium.agi.com/smallterrain'
	});
	
	cesiumWidget.centralBody.enableLighting = true;
    
	// For dynamic object camera lock on
	cesiumWidget.extend(Cesium.viewerDynamicObjectMixin);
	
	// Add dynamic CZML data source.
	var czmlDataSource = new Cesium.CzmlDataSource();
    czmlDataSource.load(czml, 'Test CZML');
    cesiumWidget.dataSources.add(czmlDataSource);
}