/**
 * Defines the hbird rootMenu module.
 * Loads the jpanel Javascript and creates the hbird menu on the page.
 */
define(["jquery", "jpanelmenu/jquery.jpanelmenu" ], function($, jPanel) {
	"use strict";
	
	var MENU_PLACEHOLDER = "hbirdRootMenu";
	var MENU_TRIGGER = "menu-trigger";

	var rootMenu = null;
	var menuList = null;
	
	function createAndAddMenuTrigger() {
		$("<img id=hbirdMenuTrigger class=" + MENU_TRIGGER + " src='../../images/metro/appbar.list.png' />").prependTo($("body"));
	}

	/**
	 * Creates a placeholder div for the menu under the id MENU_PLACEHOLER
	 */
	function createAndAddMenuPlaceholder() {
		var menu = $("<div id=" + MENU_PLACEHOLDER + " style='display: none; position: fixed; top: 64px; left: 4px;' />").prependTo($("body"));
		menuList = $("<ul id=rootMenu>").appendTo(menu);
	}

	/**
	 * Creates, adds, and switches on the jpanel menu to the MENU_PLACEHOLDER div.
	 */
	function attachMenu() {
		rootMenu = $.jPanelMenu({
			menu : "#" + MENU_PLACEHOLDER,
			keepEventHandlers : true,
			duration: 50
		});
	}

	function addMenuItem(jqObj, evtHandler) {
		console.log("Adding menu " + jqObj);
		menuList.append($("<li>").append(jqObj));
	}

	createAndAddMenuTrigger();
	createAndAddMenuPlaceholder();
	attachMenu();
	console.log("Hbird menu initialised");
	
	return {
		addMenuItem : function(jquerySelector, eventHandler) {
			addMenuItem(jquerySelector, eventHandler);
		},
		startMenu : function() {
			rootMenu.on();
		}
	};
});


