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

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.giantelectronicbrain.catfood.client.facility.Component;
import com.giantelectronicbrain.catfood.client.facility.Signal;
import com.giantelectronicbrain.catfood.client.facility.SignalBroker;
import com.giantelectronicbrain.catfood.client.facility.ViewableFacility;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;

import elemental2.dom.Event;

/**
 * A button which goes on the menubar, but isn't a menu.
 * 
 * @author tharter
 *
 */
public class MenuBarButton extends MenuButton {
	private static final Logger logger = Client.PLATFORM.getLogger(MenuBarButton.class.getName());
	private static final String DEFAULT_MENUBAR_STYLING = "menu-view w3-round-large w3-card-4";
	
	private Component target;

	public static MenuBarButton createMenuBarButton(MenuBar parent, Component target, String buttonStyle) {
		MenuBarButton button = new MenuBarButton(parent.getUiContributionPoint(), target, DEFAULT_MENUBAR_STYLING+" "+buttonStyle);
		parent.contribute(button);
		return button;
	}
	
	public MenuBarButton(Fluent parent, Component target, String buttonStyling) {
		super(parent,buttonStyling);
		setTarget(target);
	}

	public void removeTarget() {
		if(this.target != null) {
			//TODO: stuff...
		}
	}
	
	public void setTarget(Component target) {
		removeTarget();
		this.target = target;
		SignalBroker.register("ACTIVATE",this,"ACTIVATE",target);
	}
	
	@Override
	protected Function<ViewableFacility, Fluent> getRenderFunction() {
		return this::render;
	}
	
	private void onClick(Fluent fluent, Event event) {
		logger.log(Level.FINEST,"-------------------------------------------------------------------------------------------------------------------");
		getSignalBroker().dispatch("ACTIVATE", new Signal<String,Chunk>("ACTIVATE","DISPLAY",new Chunk(
				"Copyright &copy; 2020 Tod G. Harter, all rights reserved.\n"
				+ "## Version\n"
				+ "CatFood 0.0.0\n"
				,null,"About CatFood",Language.MARKDOWN,0
				)));
	}
	
	protected Fluent render(ViewableFacility menuBarButton) {
		logger.log(Level.FINEST,"Renderning MenuBarButton "+menuBarButton);
		tag = Div("menu-view "+styling+" w3-round-large w3-card-4").click((fluent,event) -> {
			onClick(fluent,event);
		});
		return tag;
	}

}
