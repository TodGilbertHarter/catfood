/**
 * 
 */
package com.giantelectronicbrain.catfood.client;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import com.giantelectronicbrain.catfood.client.chunk.ChunkController;
import com.google.gwt.core.client.EntryPoint;

import elemental.dom.Element;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * Main entry point for the CatFood VertxUI client.
 * 
 * @author tharter
 *
 */
public class Client implements EntryPoint {
	
	public static final IPlatform PLATFORM = new GWTPlatform();

	private Element mainDiv;
	private Fluent root;
	private ChunkController chunkController;

	public Client() {
		mainDiv = Fluent.document.getElementById("app-node");
		root = Fluent.getElementById("app-node");
		
		if(root == null) {
			root = body;
		}
		if(mainDiv == null) {
			mainDiv = Fluent.document.getParentElement();
		}
		
		root.p().txt("Some text").css(Css.color, "green");
		
		chunkController = new ChunkController(root,"Home");
	}

	@Override
	public void onModuleLoad() {
//		chunkController.draw();
	}
}
