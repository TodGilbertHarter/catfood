/**
 * 
 */
package com.giantelectronicbrain.catfood.client.view;

import static live.connector.vertxui.client.fluent.Fluent.Input;

import com.giantelectronicbrain.catfood.model.Chunk;

import elemental.events.Event;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

/**
 * @author tharter
 *
 */
public class ChunkView {
	private final ViewOn<Chunk> chunkVO;
	
	public ChunkView(Chunk chunk,Fluent parent) {
		chunkVO = parent.add(chunk, (fooby)-> { 
			Fluent input = Input(null,fooby.getContent());
			input.changed(this::changed);
			return input; 
		});

	}
	
	private void changed(Fluent input, Event e) {
		Fluent.window.alert("the value changed"+e);
	}

	public ViewOn<Chunk> getVO() {
		return chunkVO;
	}
}
