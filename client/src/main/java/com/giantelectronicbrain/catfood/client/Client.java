/**
 * 
 */
package com.giantelectronicbrain.catfood.client;

import static com.giantelectronicbrain.catfood.client.fluent.FluentBase.body;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.giantelectronicbrain.catfood.client.chunk.ChunkController;
import com.giantelectronicbrain.catfood.client.chunk.ChunkId;
import com.giantelectronicbrain.catfood.client.chunk.Repository;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.google.gwt.core.client.EntryPoint;
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

	private Fluent menu;
	private Fluent root;
	private ChunkController chunkController;
	private Router router;
	private Repository repo = new Repository();

	private static ArrayList<String> SCRIPTS = new ArrayList<>();
	static {
		SCRIPTS.add("/webjars/markdown-it/dist/markdown-it.js");
		SCRIPTS.add("/libs/initialize.js");
	}
	
	private static ArrayList<String> STYLESHEETS = new ArrayList<>();
	static {
		STYLESHEETS.add("/css/w3.css");
		STYLESHEETS.add("/css/all.css");
		STYLESHEETS.add("/css/catfood.css");
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

	private void createRouter() {
		Map<String,String> options = new HashMap<>();
		options.put("root", "/");
		
		this.router = new Router(options);
		
		this.router.add(RegExp.compile("/edit/id/(.*)"), (arguments) -> { // edit the chunk with the given id
			Fluent.console.log("ROUTER CHOOSING EDIT BY ID:",arguments);
			ChunkId chunkId = new ChunkId(arguments[1]);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,chunkId,true);
			} else {
				chunkController.edit(chunkId);
			}
		});
		this.router.add(RegExp.compile("/view/id/(.*)"), (arguments) -> { // view the chunk with the given id
			Fluent.console.log("ROUTER CHOOSING VIEW BY ID:",arguments);
			ChunkId chunkId = new ChunkId(arguments[1]);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,chunkId,false);
			} else {
				chunkController.view(chunkId);
			}
		});
		this.router.add(RegExp.compile("/edit/(.*)"), (arguments) -> { // edit the chunk with the given name
			Fluent.console.log("ROUTER CHOOSING EDIT BY NAME:",arguments);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,arguments[1],true);
			} else {
				chunkController.edit(arguments[1]);
			}
		});
		this.router.add(RegExp.compile("/view/(.*)"), (arguments) -> { // view the chunk with the given name
			Fluent.console.log("ROUTER CHOOSING VIEW BY NAME:",arguments);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,arguments[1],false);
			} else {
				chunkController.view(arguments[1]);
			}
		});
		this.router.add(RegExp.compile(".*"), (arguments) -> { // catch all, we will display a fixed 'root' topic for now...
			Fluent.console.log("ROUTER CHOOSING DEFAULT:",arguments);
			if(chunkController == null) {
				chunkController = new ChunkController(router,repo,root,"Home",false);
			} else {
				chunkController.view("Home");
			}
//			chunkController.view((String)null); //TODO: this isn't really quite right, but it should get us started.
		});
	}
	
	@Override
	public void onModuleLoad() {
		root = Fluent.getElementById("chunk-node");
		menu = Fluent.getElementById("menu");
		
		if(root == null) {
			root = body;
		}
		
		createRouter();
		MenuController mc = new MenuController(menu,false);
		router.kick();
	}
}
