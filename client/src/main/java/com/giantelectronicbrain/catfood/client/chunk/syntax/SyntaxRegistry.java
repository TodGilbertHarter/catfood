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
package com.giantelectronicbrain.catfood.client.chunk.syntax;

import java.util.HashMap;
import java.util.Map;

/**
 * Acts as a registry for chunk syntax classes. They can register with it
 * by calling 'registerSyntax' statically when loaded.
 * 
 * @author tharter
 *
 */
public class SyntaxRegistry {
	private static final Map<String,Syntax> registryMap = new HashMap<>();
	
	public static synchronized void register(String name, Syntax syntax) {
		registryMap.put(name,syntax);
	}

	/**
	 * Get Instance of renderer for a given syntax.
	 * 
	 * @param name
	 * @return
	 */
	public static Syntax getRenderer(String name) {
		return registryMap.get(name);
	}
}
