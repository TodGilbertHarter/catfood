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

import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Div;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.chunk.syntax.Syntax;
import com.giantelectronicbrain.catfood.client.chunk.syntax.SyntaxRegistry;
import com.giantelectronicbrain.catfood.client.facility.SignalBroker;
import com.giantelectronicbrain.catfood.client.facility.ViewableComponent;
import com.giantelectronicbrain.catfood.client.facility.ViewableFacility;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.giantelectronicbrain.catfood.client.fluent.Viewable;
import com.giantelectronicbrain.catfood.client.menu.MenuBarButton;

/**
 * Simple view of a chunk. 
 * NOTE: work on using this to replace the built-in chunk display logic in
 * the ChunkViewFactory used by ChunkController...
 * 
 * @author tharter
 *
 */
public class ChunkView implements ViewableComponent {
	private final SignalBroker signalBroker = new SignalBroker();
	private static final Logger logger = Client.PLATFORM.getLogger(ChunkView.class.getName());
	private Fluent root;			// The root object where our UI lives
	private ViewOn<Chunk> view;		// The ViewOn which is displaying the UI

	/**
	 * Create a chunk view and attach it to the given ViewableFacility.
	 * 
	 * @param chunk
	 * @param parent
	 * @return
	 */
	public static ChunkView createChunkView(Chunk chunk, ViewableFacility parent) {
		return new ChunkView(chunk,parent.getUiContributionPoint());
	}
	
	/**
	 * Create a chunk view which is attached to a fluent.
	 * 
	 * @param chunk
	 * @param root
	 * @return
	 */
	public static ChunkView createChunkView(Chunk chunk, Fluent root) {
		return new ChunkView(chunk,root);
	}
	
	public ChunkView(Chunk chunk, Fluent root) {
		this.root = root;
		view = root.add(chunk,buildView());
	}
	
	public Function<Chunk,Fluent> buildView() {
		Function<Chunk,Fluent> builder = (chunk)-> { 
				logger.log(Level.FINER,"Rendering with chunk "+chunk);
				Fluent wrapper = Div("chunk-view w3-container");
				wrapper.h1("chunk-title cf-h1font", chunk == null ? "loading" : chunk.getName());
				Fluent chunkTextDiv = wrapper.div("chunk-text cf-pfont");
				if(chunk == null) {
					chunkTextDiv.txt("...");
					wrapper.id("null_chunk_id");
				} else {
					logger.log(Level.FINEST,"Rendering chunk is not null "+chunk);
					chunkTextDiv.setInnerHTML(renderContent(chunk));
					wrapper.id(chunk.getChunkIdAsHtml());
				}
				return wrapper;
			};
		return builder;
	}
	
	private Syntax getRenderer(Chunk.Language lang) {
		return SyntaxRegistry.getRenderer(lang.name());
	}
	
	private String renderContent(Chunk chunk) {
		return this.getRenderer(chunk.getLang()).generateHTML(chunk);
	}

	@Override
	public SignalBroker getSignalBroker() {
		return signalBroker;
	}

	@Override
	public Viewable getDisplay() {
		return view;
	}
	
}
