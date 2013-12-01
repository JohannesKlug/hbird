/**
 * Defines the hbird rootMenu module.
 * Loads the jpanel Javascript and creates the hbird menu on the page.
 */
define(["jquery", "jpanelmenu/jquery.jpanelmenu.min" ], function($, jPanel) {
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

	function addMenuItem(jqObj, evtHandler) {
		console.log("Adding menu " + jqObj);
		jqObj.click(evtHandler);
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


