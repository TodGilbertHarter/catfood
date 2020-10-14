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

import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Div;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.facility.Capabilities;
import com.giantelectronicbrain.catfood.client.facility.Component;
import com.giantelectronicbrain.catfood.client.facility.Facility;
import com.giantelectronicbrain.catfood.client.facility.ViewableFacility;
import com.giantelectronicbrain.catfood.client.fluent.Css;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.giantelectronicbrain.catfood.client.fluent.Viewable;

import elemental2.dom.Event;

/**
 * @author tharter
 *
 */
public class Menu extends ViewableFacility implements MenuItem {
	private static final Logger logger = Client.PLATFORM.getLogger(MenuBar.class.getName());
	public static final String DEFAULT_IMAGE_STYLING = "fas fa-caret-right fa-2x";
	//Items which are in this menu
	protected Set<MenuItem> menuItems = new HashSet<MenuItem>();
	//True if we are actively displaying our contributions (menu items)
	protected boolean active = false;
	protected String styling;
	//Where we attach ourselves to our parent
//	protected Fluent parentComponentUiIntegrationPoint;
	//Contributions to us attach their fluents here
	protected Fluent contributionPoint;

	/**
	 * Menu Factory. Create a new Menu and contribute it to the
	 * given Menu object.
	 * 
	 * @param parent the Menu(Bar) we will contribute to
	 * @return
	 */
	public static Menu createMenu(Menu parent) {
		Menu menu = new Menu(parent.getUiContributionPoint(),DEFAULT_IMAGE_STYLING);
		parent.contribute(menu);
		return menu;
	}
	
	/**
	 * Create a menu which is attached to the given fluent, and has the
	 * provided image styling.
	 * 
	 * @param parent Fluent where this is attached.
	 * @param imageStyling classes to add to the rendered menu.
	 */
	public Menu(Fluent parent, String imageStyling) {
		super(buildCapabilities());
		logger.log(Level.FINEST,"Building a Menu attached to "+parent);
//		this.parentComponentUiIntegrationPoint = parent;
		this.styling = imageStyling;
		logger.log(Level.FINEST,"WTF IS THE RENDER FUNCTION "+this.getRenderFunction());
//		viewOn = new ViewOn<ViewableFacility>(this,this.getRenderFunction());
//		parent.add(viewOn);
		viewOn = parent.add(this,this.getRenderFunction());
	}

	/**
	 * Return the fluent which provides the integration point for contributions.
	 * 
	 * @return Fluent which should be the contribution's fluent's parent.
	 */
	public Fluent getUiContributionPoint() {
		return contributionPoint;
	}
	
	/**
	 * Add a MenuItem to this menu. It could be a sub-menu, or anything else
	 * which can participate in a menu.
	 * 
	 * @param item The item to contribute.
	 */
	public Component contribute(Component component) {
		menuItems.add((MenuItem) component);
		return component;
	}
	
	private void activate(boolean activate) {
		this.active = activate;
		viewOn.sync();
	}
	
	private void activate() {
		activate(!this.active);
	}
	
	protected BiConsumer<Fluent, Event> onClick = (fluent,event) -> {
		logger.log(Level.FINER,"main button was clicked, active is "+active);
		activate();
	};
	
	protected Function<ViewableFacility, Fluent> getRenderFunction() {
		return this::render;
	}

	private Fluent render(ViewableFacility menuVf) {
		logger.log(Level.FINEST,"Renderning Menu "+menuVf+", active is: "+active);
		Fluent menu = Div("menu-view "+styling+" w3-round-large w3-card-4").click((fluent,event) -> {
			onClick.accept(fluent,event);
		});
		contributionPoint = menu.ul("menu").css(Css.display, active ? "block" : "none");
		for(MenuItem menuItem : menuItems) {
			Viewable display = menuItem.getDisplay();
			if(display instanceof ViewOn<?>) {
				((ViewOn<?>)display).generate(contributionPoint);
			} else {
				contributionPoint.add(display);
			}
		}
		return contributionPoint;
	}
	
	private static Capabilities buildCapabilities() {
		return new Capabilities(Facility.CAP_EXTENSIBLE);
	}
	
	@Override
	public Set<? extends Facility> getExtensionPoints() {
		return null; //Set.copyOf(this.menus);
	}

	@Override
	public Set<? extends Facility> getExtensionPoints(Capabilities capabilities) {
		return null; // this.menus; //TODO: Fix this...
	}

}
