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

import com.giantelectronicbrain.catfood.client.fluent.Att;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;

import elemental2.dom.HTMLTextAreaElement;

/**
 * @author tharter
 *
 */
public class ChunkEditorViewFactory {

	public static ViewOn<Chunk> createChunkView(Fluent parent, Consumer<Chunk> saveHandler, Consumer<Chunk> cancelHandler) {
		return createChunkView(null,parent, saveHandler, cancelHandler);
	}
	
	public static ViewOn<Chunk> createChunkView(Chunk chunk, Fluent parent, Consumer<Chunk> saveHandler, Consumer<Chunk> cancelHandler) {
		Fluent.console.log("Building editor with chunk: ",chunk);
		return parent.add(chunk, (newChunk)-> { 
			Fluent editor = Div("chunk-editor w3-container").id("chunk_editor");
			Fluent titleInput = editor.input("chunk-editor-title w3-input","input");
			Fluent textInput = editor.textarea().classs("chunk-editor-area w3-input").att(Att.cols, "40").att(Att.rows,"10");
			if(newChunk == null) {
				titleInput.att(Att.placeholder,"name...");
				textInput.att(Att.placeholder,"enter content here...");
			} else {
				titleInput.att(Att.value,newChunk.getName());
				textInput.txt(newChunk.getContent());
			}
			Fluent buttonBar = editor.div("chunk-editor-bar w3-bar");
			buttonBar.button("chunk-editor-save far fa-save w3-button w3-blue", "BUTTON", "")
				.click((fluent,event) -> {
					newChunk.setName(titleInput.domValue());
					String foo = ((HTMLTextAreaElement)textInput.dom()).value;
					newChunk.setContent(foo);
					saveHandler.accept(newChunk);
				});
			buttonBar.button("chunk-editor-cancel far fa-window-close w3-button w3-red", "BUTTON", "")
				.click((fluent,event) -> {
					cancelHandler.accept(newChunk);
				});
			return editor;
		});
	}
	
}
