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

/**
 * A component is more general than a Facility. It may not be able
 * to act as a Facility itself, but it can be contributed to a facility
 * for use by it at a contribution point.
 * 
 * Note that components don't provide Capabilities. 
 * 
 * @author tharter
 *
 */
public interface Component {
	
	/**
	 * Get the component's signal broker. This is really only abstract because
	 * we can't make Component a base class, silly Java...
	 * 
	 * @return The SignalBroker for this Component. You better have/make one...
	 */
	public abstract SignalBroker getSignalBroker();
}
