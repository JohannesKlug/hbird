var MENU_PLACEHOLDER = "hbirdRootMenu";
var MENU_TRIGGER = "menu-trigger";

var rootMenu;
var menuList;

/**
 * Defines the hbird rootMenu module.
 * Loads the jpanel Javascript and creates the hbird menu on the page.
 */
define(["jquery", "jpanelmenu/jquery.jpanelmenu.min" ], function($, jPanel) {
	createAndAddMenuTrigger();
	createAndAddMenuPlaceholder();
	attachMenu();
	console.log("Hbird menu initialised");
	
	return {
		addMenuItem : function(htmlElementMenuItem) {
			addMenuItem(htmlElementMenuItem);
		},
		startMenu : function() {
			activateMenu();
		}
	};
});

function activateMenu() {
	rootMenu.on();
}

function createAndAddMenuTrigger() {
	var trigger = $("<div id=hbirdMenuTrigger class=" + MENU_TRIGGER + ">").prependTo($("body"));
	trigger.prepend("<img id=menuTriggerImg src='../../images/metro/appbar.list.png' />");
}

/**
 * Creates a placeholder div for the menu under the id MENU_PLACEHOLER
 */
function createAndAddMenuPlaceholder() {
	var menu = $("<div id=" + MENU_PLACEHOLDER + " style='display: none; position: fixed; top: 64px; left: 4px;' />").prependTo($("body"));
	menuList = $("<ul>").appendTo(menu);
}

/**
 * Creates, adds, and switches on the jpanel menu to the MENU_PLACEHOLDER div.
 */
function attachMenu() {
	rootMenu = $.jPanelMenu({
		menu : "#" + MENU_PLACEHOLDER
	});
}

function addMenuItem(element) {
	console.log("Adding menu " + element);
	menuList.append($("<li>" + element + "</li>"));
}


