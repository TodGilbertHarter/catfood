/**
 * 
 */
package com.giantelectronicbrain.catfood.client;

import com.google.gwt.core.client.EntryPoint;

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.Event;
import live.connector.vertxui.client.fluent.Fluent;
import static live.connector.vertxui.client.fluent.Fluent.*;

/**
 * Main entry point for the CatFood VertxUI client.
 * 
 * @author tharter
 *
 */
public class CatFood implements EntryPoint {

	private Element mainDiv;
	
	public CatFood() {
		mainDiv = Fluent.document.getElementById("app-node");
		
		Fluent div = Div(null,"Hello World");
		
		mainDiv.appendChild((Node) div);
		mainDiv.setOnclick(this::clicked);
	}

	private void clicked(Event e) {
		Fluent.window.alert("I got clicked "+e);
	}
	
	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		
	}
}
