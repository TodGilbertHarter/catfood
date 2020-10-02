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

import java.util.function.Consumer;
import java.util.function.Function;

import com.giantelectronicbrain.catfood.client.chunk.syntax.Syntax;
import com.giantelectronicbrain.catfood.client.chunk.syntax.markdown.MarkDownIt;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;

/**
 * @author tharter
 *
 */
public class ChunkViewFactory {
	private static Syntax renderer = new MarkDownIt();

	public static ViewOn<Chunk> createChunkView(Fluent parent,Consumer<Chunk> editHandler) {
		return createChunkView(null,parent, editHandler);
	}
	
	public static ViewOn<Chunk> createChunkView(Chunk chunk, Fluent parent,Consumer<Chunk> editHandler) {
		return parent.add(chunk, buildView(editHandler));
	}

	public static Function<Chunk,Fluent> buildView(Consumer<Chunk> editHandler) {
		Function<Chunk,Fluent> builder = (chunk)-> { 
				Fluent.console.log("Rendering with chunk {}",chunk);
				Fluent wrapper = Div("chunk-view w3-container");
				wrapper.h1("chunk-title cf-h1font", chunk == null ? "loading" : chunk.getName());
				Fluent chunkTextDiv = wrapper.div("chunk-text cf-pfont");
				if(chunk == null) {
					chunkTextDiv.txt("...");
					wrapper.id("null_chunk_id");
				} else {
					Fluent.console.log(" and chunk is not null {}",chunk);
					chunkTextDiv.setInnerHTML(renderContent(chunk));
					wrapper.id(chunk.getChunkIdAsHtml());
				}
				wrapper.button("chunk-edit-button far fa-edit w3-button w3-blue", "BUTTON", "")
					.click((fluent,event) -> {
						editHandler.accept(chunk);
					});
				return wrapper;
			};
		return builder;
	}
	
	private static String renderContent(Chunk chunk) {
		return renderer.generateHTML(chunk);
	}
}
