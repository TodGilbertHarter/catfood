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

import com.giantelectronicbrain.catfood.client.fluent.Css;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;

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
		this.menu = MenuViewFactory.createMenuView(root,active,this::handleMainButtonClick);
		activate(active);
	}
	
	public MenuController(Fluent root) {
		this(root,false);
	}
	
	private void activate(boolean activate) {
		this.active = activate;
		Fluent mblock = (Fluent) this.menu.getChildren().get(0);
Fluent.console.log("WTF IS mblock: ",mblock,",",mblock.tag());
Fluent.console.log("Before hide call activate is: ",true,", mblock display style is now: ",mblock.css(Css.display));
		activate = !activate;
Fluent.console.log("calling hide with ",activate);
		mblock.hide(activate);
Fluent.console.log("After hide call mblock display style is now: ",mblock.css(Css.display));
	}
	
	private void activate() {
		activate(!this.active);
	}
	
	private Void handleMainButtonClick() {
		Fluent.console.log("main button was clicked, active is ",active);
		activate();
		return null;
	}
}
