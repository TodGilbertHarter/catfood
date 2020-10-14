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

import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Li;

import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.facility.Capabilities;
import com.giantelectronicbrain.catfood.client.facility.Component;
import com.giantelectronicbrain.catfood.client.facility.Facility;
import com.giantelectronicbrain.catfood.client.facility.ViewableFacility;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.giantelectronicbrain.catfood.client.fluent.Viewable;

/**
 * @author tharter
 *
 */
public class MenuButton extends ViewableFacility implements MenuItem {
	private static final Logger logger = Client.PLATFORM.getLogger(MenuButton.class.getName());
	private static final String DEFAULT_MENU_STYLING = "w3-button w3-round-large cf-intfont"; // styling for when it is in a menu
	private static final String DEFAULT_MENUBAR_STYLING = "menu-view w3-round-large w3-card-4";
	protected String styling;
//	private Fluent parentComponentUiIntegrationPoint;
	protected Fluent tag;

	public static MenuButton createMenuBarButton(MenuBar parent, String buttonStyle) {
		return createMenuButton(parent,DEFAULT_MENUBAR_STYLING+" "+buttonStyle);
	}
	
	public static MenuButton createMenuButton(Menu parent) {
		return createMenuButton(parent,DEFAULT_MENU_STYLING);
//		MenuButton button = new MenuButton(parent.getUiContributionPoint(),DEFAULT_MENU_STYLING);
//		parent.contribute(button);
//		return button;
	}

	public static MenuButton createMenuButton(Menu parent, String styling) {
		MenuButton button = new MenuButton(parent.getUiContributionPoint(),styling);
		parent.contribute(button);
		return button;
	}
	
	public MenuButton(Fluent parent, String buttonStyling) {
		super(buildCapabilities());
//		this.parentComponentUiIntegrationPoint = parent;
		this.styling = buttonStyling;
		viewOn = new ViewOn<ViewableFacility>(this,this.getRenderFunction());
		parent.add(viewOn);
	}
	
	private static Capabilities buildCapabilities() {
		return new Capabilities(Facility.CAP_EXTENSIBLE);
	}
	
	@Override
	protected Function<ViewableFacility, Fluent> getRenderFunction() {
		return this::render;
	}
	
	protected Fluent render(ViewableFacility menu) {
//	private Function<ViewableFacility, Fluent> render = (menu) -> {
		Fluent ul = menu.getUiContributionPoint();
		tag = Li("").a(styling, "something", "/something", null);		
		return tag;
	}
	

	@Override
	public Set<? extends Facility> getExtensionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends Facility> getExtensionPoints(Capabilities capabilities) {
		// TODO Auto-generated method stub
		return null;
	}

}
