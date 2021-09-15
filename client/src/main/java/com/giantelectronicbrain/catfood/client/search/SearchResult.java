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
package com.giantelectronicbrain.catfood.client.search;

import com.giantelectronicbrain.catfood.client.Router;
import com.giantelectronicbrain.catfood.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;

import elemental2.dom.Event;

/**
 * @author tharter
 *
 */
public class SearchResult {
	
	private static final String DEFAULT_ROW_STYLE = "sr-row";
	private final Chunk contents;
	private final Router router;
	private final String rowStyle;
	
	public SearchResult(Chunk contents, Router router, String rowStyle) {
		this.contents = contents;
		this.router = router;
		this.rowStyle = rowStyle == null ? DEFAULT_ROW_STYLE : rowStyle;
	}
	
	public SearchResult(Chunk contents, Router router) {
		this(contents,router,null);
	}
	
	public String summarize() {
		return contents.getContent().substring(0,80) + "...";
	}
	
	public native String localDT(Long timestamp) /*-{
		return new Date(timestamp).toLocaleString();
	}-*/;
	
	public Fluent render() {
		Fluent tableRow = Fluent.Tr("sr-row "+rowStyle);
		tableRow.td("sr-cell",null)
			.a("sr-link", contents.getName(), null, this::handleClick);
		tableRow.td("sr-cell",summarize());
		tableRow.td("sr-cell",localDT(contents.getLastUpdated()));
		return tableRow;
	}
	
	public void handleClick(Fluent fluent, Event event) {
		router.pushState(null, null, "/view/id/"+contents.getChunkId().getChunkId());
	}
}
