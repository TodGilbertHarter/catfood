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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.Router;
import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.Repository;
import com.giantelectronicbrain.catfood.client.facility.Signal;
import com.giantelectronicbrain.catfood.client.facility.SignalBroker;
import com.giantelectronicbrain.catfood.client.facility.ViewableComponent;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.giantelectronicbrain.catfood.client.fluent.Viewable;

/**
 * Search results panel. This component's find() method can be called to initiate an asynchronous
 * search. The results will be displayed when the search completes. It will also respond to messages
 * on its "SEARCH" slot, allowing integration with other components (IE SearchEntry). 
 * 
 * 
 * 
 * @author tharter
 *
 */
public class SearchResults implements ViewableComponent {
	private static final Logger logger = Client.PLATFORM.getLogger(SearchResults.class.getName());
	private final SignalBroker signalBroker = new SignalBroker();
	private final Router router;
	private final Repository repository;
	private ViewOn<List<Chunk>> display;

	public static SearchResults createSearchResults(Router router, Repository repository, Fluent parent) {
		return new SearchResults(parent,repository,router);
	}

	/**
	 * Create a search results display component. This will be rendered onto the
	 * provided parent fluent when a search returns results.
	 * 
	 * @param parent fluent to attach the component to
	 * @param repository source for chunk data
	 * @param router the application router
	 */
	public SearchResults(Fluent parent, Repository repository, Router router) {
		this.repository = repository;
		this.router = router;
		getSignalBroker().registerSlot("SEARCH", this::searchStarted);
		this.display = parent.add(null, this::renderResults);
	}

	public void hide(boolean hide) {
		if(this.display != null && this.display.getView() != null)
			this.display.getView().hide(hide);
	}
	
	private Fluent renderResults(List<Chunk> results) {
		if(results == null) return null;
		Fluent ui = Fluent.Div("sr-view w3-container");
		Fluent resultsTable = ui.table("sr-table w3-table w3-striped w3-bordered");
		Fluent tHead = resultsTable.thead().tr("w3-camo-olive");
		tHead.th(null,"Topic Name");
		tHead.th(null,"Summary");
		tHead.th(null,"Last Updated");
		boolean even = false;
		Fluent tbody = resultsTable.tbody();
		for(Chunk result : results) {
			String rowStyle = even ? "sr-row sr-row-even" : "sr-row sr-row-odd";
			tbody.add(new SearchResult(result,router,rowStyle).render());
			even = !even;
		}
		addFooter(ui);
		return ui;
	}
	
	private void addFooter(Fluent ui) {
		//TODO: this would hold pagination and such
	}
	
	private void searchStarted(Signal<?,?> signal) {
		logger.log(Level.FINE,"Searching for "+signal.getContext());
		router.pushState(null, null, "/find/name/"+signal.getContext());
	}

	/**
	 * Initiate a search for chunks matching the given name pattern. When
	 * results are returned, rendering will be performed.
	 * 
	 * @param pattern topic name pattern
	 */
	public void find(String pattern) {
		repository.findChunk(pattern, this::handleSearchResults);
	}

	private void handleSearchResults(Integer status, List<Chunk> results) {
		logger.log(Level.FINER,"Got search results");
		display.state(results);
	}
	
	@Override
	public SignalBroker getSignalBroker() {
		return this.signalBroker;
	}

	@Override
	public Viewable getDisplay() {
		return this.display;
	}
}
