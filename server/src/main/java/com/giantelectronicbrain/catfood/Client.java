/**
 * 
 */
package com.giantelectronicbrain.catfood;

import java.util.ArrayList;

/**
 * Main entry point for the CatFood VertxUI client.
 * 
 * @author tharter
 *
 */
public class Client implements IClient {
	
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

}
