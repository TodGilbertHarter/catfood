/**
 * This software is Copyright (C) 2020 Tod G. Harter. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.giantelectronicbrain.catfood.client.chunk;

import static live.connector.vertxui.client.fluent.Fluent.Input;

import java.util.function.BiConsumer;

import elemental.events.UIEvent;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;

/**
 * View for a chunk.
 * 
 * @author tharter
 *
 */
public class ChunkView {
	private final ViewOn<Chunk> chunkVO;

	/**
	 * Create a component which has no initial state.
	 * 
	 * @param parent the parent fluent of this view.
	 * @param changedHandler handler to run if the content changes due to some User action.
	 */
	public ChunkView(Fluent parent, BiConsumer<Fluent,UIEvent> changedHandler) {
		chunkVO = parent.add(null, (fooby)-> { 
			Fluent input = Input(null,null);
			input.changed(changedHandler).att(Att.placeholder,"enter content here...");
			return input;
		});
		draw();
	}
	
	/**
	 * Create a UI component for a Chunk, with an initial value. This will be the state of the compoenent.
	 * 
	 * @param chunk the initial state of the component.
	 * @param parent the parent fluent where this is displayed.
	 * @param changedHandler handler to run if the content changes due to some User action.
	 */
	public ChunkView(Chunk chunk, Fluent parent, BiConsumer<Fluent,UIEvent> changedHandler) {
		chunkVO = parent.add(chunk, (fooby)-> { 
			Fluent input = Input(null,fooby.getContent());
			input.changed(changedHandler).att(Att.value,chunk.getContent());
			return input;
		});
		draw();
	}
	
	/**
	 * Attach a new model to this view.
	 * 
	 * @param newModel the new model to attach
	 */
	public void update(Chunk newModel) {
		this.chunkVO.state(newModel);
	}

	/**
	 * Disable (true) or enable (false) interaction with the view. This won't
	 * change the appearance, but might grey out editing components, etc.
	 * 
	 * @param flag
	 */
	public void disable(boolean flag) {
		this.chunkVO.getView().disabled(flag);
	}
	
	/**
	 * Render the view, given the current state of the existing model.
	 */
	public void draw() {
		this.chunkVO.sync();
	}
}
