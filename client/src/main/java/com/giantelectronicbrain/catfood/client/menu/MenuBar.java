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
package com.giantelectronicbrain.catfood.client.menu;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.facility.Capabilities;
import com.giantelectronicbrain.catfood.client.facility.Facility;
import com.giantelectronicbrain.catfood.client.facility.ViewableFacility;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Div;;

/**
 * A menu bar is basically a variation on a menu which is displayed horizontally.
 * Normally it would contain other menus, although it could contain any type of
 * menu item as a contribution.
 * 
 * @author tharter
 *
 */
public class MenuBar extends Menu {
	private static final Logger logger = Client.PLATFORM.getLogger(MenuBar.class.getName());
	private static final String DEFAULT_BAR_STYLING = "menu-bar";
	
	/**
	 * Creates a bar with default styling.
	 * 
	 * @param root
	 * @return
	 */
	public static MenuBar createMenuBar(Fluent root) {
		logger.log(Level.INFO,"Creating a MenuBar");
		return new MenuBar(root);
	}
	
	/**
	 * Create a menu bar with the default bar styling.
	 * 
	 * @param root
	 */
	public MenuBar(Fluent root) {
		super(root,DEFAULT_BAR_STYLING);
	}
	
	private Fluent render(ViewableFacility menuBar) {
		logger.log(Level.FINEST,"Rendering menubar "+menuBar);
		contributionPoint = Div(styling).id("main-menu-bar");
		return contributionPoint;
	}
	
	protected Function<ViewableFacility, Fluent> getRenderFunction() {
		return this::render;
	}
	
	protected static Capabilities buildCapabilities() {
		return new Capabilities(Facility.CAP_EXTENSIBLE);
	}
	
}
