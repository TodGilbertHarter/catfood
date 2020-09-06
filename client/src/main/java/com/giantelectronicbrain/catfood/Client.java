/**
 * 
 */
package com.giantelectronicbrain.catfood;

import static live.connector.vertxui.client.fluent.FluentBase.body;

import com.giantelectronicbrain.catfood.client.view.ChunkView;
import com.giantelectronicbrain.catfood.model.Chunk;
import com.giantelectronicbrain.catfood.model.ChunkId;
import com.google.gwt.core.client.EntryPoint;

import elemental.dom.Element;
import elemental.events.Event;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * Main entry point for the CatFood VertxUI client.
 * 
 * @author tharter
 *
 */
public class Client implements EntryPoint {

	private Element mainDiv;
	private Fluent root;
	private ChunkView chunkView;

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
		
		Chunk chunk = new Chunk("chunk contents",new ChunkId("cid"));
		chunkView = new ChunkView(chunk,root);
	}

	@Override
	public void onModuleLoad() {
		chunkView.getVO().sync();
	}
}
