/**
 * 
 */
package com.giantelectronicbrain.catfood.client;

import static com.giantelectronicbrain.catfood.client.fluent.FluentBase.body;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.ChunkController;
import com.giantelectronicbrain.catfood.client.chunk.ChunkId;
import com.giantelectronicbrain.catfood.client.chunk.Repository;
import com.giantelectronicbrain.catfood.client.dialog.Dialog;
import com.giantelectronicbrain.catfood.client.facility.Component;
import com.giantelectronicbrain.catfood.client.facility.SignalBroker;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.menu.Menu;
import com.giantelectronicbrain.catfood.client.menu.MenuBar;
import com.giantelectronicbrain.catfood.client.menu.MenuBarButton;
import com.giantelectronicbrain.catfood.client.menu.MenuButton;
import com.giantelectronicbrain.catfood.client.search.SearchEntry;
import com.giantelectronicbrain.catfood.client.search.SearchResults;
import com.google.gwt.regexp.shared.RegExp;

import elemental2.dom.Element;

/**
 * Main entry point for the CatFood VertxUI client.
 * 
 * @author tharter
 *
 */
public class Client implements IClient {
	
	public static final IPlatform PLATFORM = new GWTPlatform();
	private static final Logger logger = PLATFORM.getLogger(Client.class.getName());

	private Fluent menu;
	private Fluent root;
	private ChunkController chunkController;
	private Router router;
	private Repository repo = new Repository();
	private SearchResults searchResults;

	private static ArrayList<String> SCRIPTS = new ArrayList<>();
	static {
		SCRIPTS.add("/webjars/markdown-it/dist/markdown-it.js");
		SCRIPTS.add("/libs/markdown-it-wikilinks.js");
		SCRIPTS.add("/libs/initialize.js");
	}
	
	private static ArrayList<String> STYLESHEETS = new ArrayList<>();
	static {
		STYLESHEETS.add("/css/w3.css");
		STYLESHEETS.add("/css/all.css");
		STYLESHEETS.add("/css/catfood.css");
		STYLESHEETS.add("/css/w3-colors-camo.css");
	}
	
	public ArrayList<String> getCss() {
		return STYLESHEETS;
	}
	
	public ArrayList<String> getScripts() {
		return SCRIPTS;
	}
	
	public String getApplicationTitle() {
		return "CatFood: Intelligent Wiki";
	}

	
	/**
	 * Note that this might be instantiated by the server in order to
	 * determine how to build the home page.
	 */
	public Client() {
	}

	/*
	 * Mobile browser detection, see https://developer.mozilla.org/en-US/docs/Web/HTTP/Browser_detection_using_the_user_agent
	 * for details about this detection method. It is most recommended currently...
	 */
	private static native boolean ISMOBILE() /*-{
		return navigator.userAgent.indexOf("Mobi" !== -1);
	}-*/;
	
	/*
	 * Read this to tell you if this is a mobile browser.
	 */
	public Boolean isMobile() { return ISMOBILE(); }
	
	private void createRouter() {
		Map<String,String> options = new HashMap<>();
		options.put("root", "/");
		
		this.router = new Router(options);
		
		this.router.add(RegExp.compile("/edit/id/(.*)"), (arguments) -> { // edit the chunk with the given id
			Fluent.console.log("ROUTER CHOOSING EDIT BY ID:",arguments);
			hideSearchResults(true);
			ChunkId chunkId = new ChunkId(arguments[1]);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,chunkId,true);
			} else {
				chunkController.edit(chunkId);
			}
		});
		this.router.add(RegExp.compile("/view/id/(.*)"), (arguments) -> { // view the chunk with the given id
			Fluent.console.log("ROUTER CHOOSING VIEW BY ID:",arguments);
			hideSearchResults(true);
			ChunkId chunkId = new ChunkId(arguments[1]);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,chunkId,false);
			} else {
				chunkController.view(chunkId);
			}
		});
		this.router.add(RegExp.compile("/edit/(.*)"), (arguments) -> { // edit the chunk with the given name
			Fluent.console.log("ROUTER CHOOSING EDIT BY NAME:",arguments);
			hideSearchResults(true);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,arguments[1],true);
			} else {
				chunkController.edit(arguments[1]);
			}
		});
		this.router.add(RegExp.compile("/view/(.*)"), (arguments) -> { // view the chunk with the given name
			Fluent.console.log("ROUTER CHOOSING VIEW BY NAME:",arguments);
			hideSearchResults(true);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,arguments[1],false);
			} else {
				chunkController.view(arguments[1]);
			}
		});
		this.router.add(RegExp.compile("/new"), (arguments) -> { // create a new empty chunk and edit it
			Fluent.console.log("ROUTER CHOOSING VIEW BY NAME:",arguments);
			hideSearchResults(true);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,(String)null,true);
			} else {
				chunkController.edit((Chunk)null);
			}
		});
		this.router.add(RegExp.compile("/find/name/(.*)"), (arguments) -> {
			logger.log(Level.FINER,"Router Choosing find "+arguments[1]);
			if(chunkController != null)
				chunkController.hide(true);
			searchResults.find(arguments[1]);
			hideSearchResults(false);
		});
		this.router.add(RegExp.compile(".*"), (arguments) -> { // catch all, we will display a fixed 'root' topic for now...
			Fluent.console.log("ROUTER CHOOSING DEFAULT:",arguments);
			hideSearchResults(true);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,"Home",false);
			} else {
				chunkController.view("Home");
			}
		});
	}
	
	/**
	 * @param hide
	 */
	private void hideSearchResults(boolean hide) {
		searchResults.hide(hide);
	}

	@Override
	public void onModuleLoad() {
		root = Fluent.getElementById("chunk-node");
		menu = Fluent.getElementById("menu");
		logger.log(Level.INFO,"Performing onModuleLoad, root is "+((Element)root.dom()).innerHTML);
		
		if(root == null) {
			root = body;
		}
		
		
		createRouter();
		
		logger.log(Level.INFO,"Going to attach menubar to"+((Element)menu.dom()).innerHTML);
		MenuBar mBar = MenuBar.createMenuBar(menu);
		Menu aMenu = Menu.createMenu(mBar);
		MenuButton mButton = MenuButton.createMenuButton(aMenu);

		Dialog aboutDialog = Dialog.createDialog(root);
		MenuBarButton infoButton = MenuBarButton.createMenuBarButton(mBar,aboutDialog,"fas fa-cat fa-2x w3-right");
		searchResults = SearchResults.createSearchResults(router,repo,root);
		SearchEntry searchBox = SearchEntry.creatSearchEntry(mBar,(Component)searchResults,null,null);
		SignalBroker.register("ACTIVATE", searchBox, "SEARCH", searchResults);

		router.kick();
	}
}
