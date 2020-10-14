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
package com.giantelectronicbrain.catfood.client.facility;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes the features of a facility. Either the ones it has, or the ones which a
 * potential consumer wants.
 * 
 * @author tharter
 *
 */
public class Capabilities {
	private final Set<String> contents = new HashSet<>();
	
	/**
	 * Construct a capabilities which contains all the given attributes.
	 * 
	 * @param values
	 */
	public Capabilities(String...values) {
		Collections.addAll(contents, values);
	}
	
	/**
	 * Construct a Capabilities object with the given attributes.
	 * 
	 * @param contents the set of attributes this capabilities represents.
	 */
	public Capabilities(Set<String> contents) {
		this.contents.addAll(contents);
	}
	
	/**
	 * Return true if the other Capabilities are entirely satisfied by this
	 * capabilities object. 
	 * 
	 * @param other the object which is querying this one.
	 * 
	 * @return true if we meet all the desired capabilities, false otherwise.
	 */
	public boolean hasAll(Capabilities other) {
		return this.contents.containsAll(other.contents);
	}
}
