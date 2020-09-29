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
package com.giantelectronicbrain.catfood.client;

import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * @author tharter
 *
 */
public class MenuController {
	private Fluent root;
	private Fluent menu;
	private boolean active;
	
	public MenuController(Fluent root, boolean active) {
		this.active = active;
		this.root = root;
		this.menu = MenuViewFactory.createMenuView(active,this::handleMainButtonClick);
		this.root.add(this.menu);
	}
	
	public MenuController(Fluent root) {
		this(root,false);
	}
	
	private Void handleMainButtonClick() {
		Fluent.console.log("main button was clicked, active is ",active);
		if(active)
			menu.css(Css.display,"none");
		else
			menu.css(Css.display,"block");
		active = !active;
		return null;
	}
}
