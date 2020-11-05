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

import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Div;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.facility.Component;
import com.giantelectronicbrain.catfood.client.facility.Signal;
import com.giantelectronicbrain.catfood.client.facility.ViewableFacility;
import com.giantelectronicbrain.catfood.client.fluent.Css;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.menu.MenuBar;
import com.giantelectronicbrain.catfood.client.menu.MenuBarButton;

import elemental2.dom.Event;

/**
 * @author tharter
 *
 */
public class SearchEntry extends MenuBarButton {
	private static final Logger logger = Client.PLATFORM.getLogger(SearchEntry.class.getName());
	private static final String DEFAULT_SEARCHENTRY_STYLING = "search-box w3-round-xxlarge w3-card-4 w3-right";
	private static final String DEFAULT_SEARCHICON_STYLING = "fas fa-search fa-2x search-icon";
	private Fluent searchBox;
	private final String entryStyling;
	
	/**
	 * @param mBar
	 * @param component
	 * @param string
	 * @return
	 */
	public static SearchEntry creatSearchEntry(MenuBar mBar, Component target, String buttonStyling, String entryStyling) {
		return new SearchEntry(mBar.getUiContributionPoint(), target, buttonStyling, entryStyling);
	}

	
	/**
	 * @param parent
	 * @param target
	 * @param buttonStyling
	 */
	public SearchEntry(Fluent parent, Component target, String buttonStyling, String entryStyling) {
		super(parent, target, DEFAULT_SEARCHICON_STYLING);
		this.entryStyling = DEFAULT_SEARCHENTRY_STYLING;
//		super(parent, target, buttonStyling == null ? DEFAULT_SEARCHICON_STYLING : buttonStyling);
//		this.entryStyling = entryStyling == null ? DEFAULT_SEARCHENTRY_STYLING : entryStyling;
	}

	private void onClick(Fluent fluent, Event event) {
		logger.log(Level.FINEST,"-------------------------------------------------------------------------------------------------------------------");
		getSignalBroker().dispatch("ACTIVATE", new Signal<String,String>("SEARCH","SEARCHTEXT",searchBox.domValue()));
	}
	
	protected Fluent render(ViewableFacility menuBarButton) {
		logger.log(Level.FINEST,"Renderning MenuBarButton "+menuBarButton);
		tag = Div("search-box w3-round-xxlarge w3-card-4 w3-right");
		searchBox = tag.input("search-text cf-enterfont","INPUT");
		tag.span(styling).click((fluent,event) -> {
			onClick(fluent,event);
		});
		return tag;
	}

}
