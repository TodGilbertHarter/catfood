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

import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;

import elemental.events.UIEvent;
import elemental.html.HtmlElement;
import elemental.html.InputElement;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * The Controller for Chunks.
 * 
 * @author tharter
 *
 */
public class ChunkController {
	private ChunkView view;
	private Chunk model;
	private Repository chunkRepo;
	
	public ChunkController(Fluent root, String topic) {
		Chunk chunk = new Chunk("loading...",null,topic,Language.MARKDOWN);
		this.model = chunk;
		this.view = new ChunkView(model,root,this::changed);
		this.view.disable(true);
		this.chunkRepo = new Repository();
		chunkRepo.findChunkByName(model.getName(), this::updated);
	}

	public ChunkController(Fluent root, Chunk model) {
		this.model = model;
		this.view = new ChunkView(model,root,this::changed);
	}
	
	public ChunkController(Fluent root) {
		this.chunkRepo = new Repository();
		this.view = new ChunkView(root,this::changed);
	}
	
	@SuppressWarnings("deprecation")
	private void changed(Fluent fluent, UIEvent event) { 
		Chunk chunk = model;
		if(chunk == null) {
			@SuppressWarnings("deprecation")
			InputElement target = (InputElement) event.getTarget();
			Fluent.console.log("The Target is "+target);
			String content = target.getValue();
			Fluent.console.log("The content is "+content);
			chunk = new Chunk(content,null,"fooby",Language.MARKDOWN);
			updated(chunk);
		}
	}
	
	private void updated(Chunk newModel) {
		this.model = newModel;
		view.update(model);
		view.draw();
	}
	
	/*
	 * handler for responses from the repository.
	 */
	private void updated(Integer status, Chunk newModel) {
		Fluent.console.log("we got an updated thingy "+newModel.toString()+", status was "+status);
		if(status == 200) {
			updated(newModel);
			this.view.disable(false); // re-enable the view if it was disabled
		} 
	}
	
/*	public ChunkController setModel(Chunk model) {
		this.model = model;
		return this;
	}
	
	public ChunkController setView(ChunkView view) {
		this.view = view;
		return this;
	} */
	
	
}
