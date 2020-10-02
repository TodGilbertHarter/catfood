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

import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Div;

import java.util.function.Supplier;

import com.giantelectronicbrain.catfood.client.fluent.Fluent;

/**
 * Construct the CatFood action menu.
 * 
 * @author tharter
 *
 */
public class MenuViewFactory {

	public static Fluent createMenuView(Fluent parent, boolean active, Supplier<Void> mainClickHandler) {
		Fluent menu = parent.div("menu-view fas fa-cat fa-2x w3-round-large w3-card-4").click((fluent,event) -> {
			mainClickHandler.get();
		});
		Fluent ul = menu.ul("menu");
				ul.li("").a("w3-button w3-round-large cf-intfont", "something", "/something", null);
				ul.li("").a("w3-button w3-round-large cf-intfont", "something", "/something", null);
				
		return menu;
	}
}
