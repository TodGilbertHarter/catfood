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

import java.util.Set;
import java.util.function.Function;


/**
 * This is the base class for facilities. It can be extended to produce
 * various facility types.
 * 
 * @author tharter
 *
 */
public abstract class Facility implements Component{
	public static final String CAP_EXTENSIBLE = "CAP_EXTENSIBLE";
	private final SignalBroker signalBroker = new SignalBroker();
	
	private final Capabilities capabilities;
	
	protected Facility(Capabilities capabilities) {
		this.capabilities = capabilities;
	}
	
	public SignalBroker getSignalBroker() {
		return this.signalBroker;
	}
	
	/**
	 * Get a list of the capabilities of this particular facility.
	 * 
	 * @return Capabilities, a descriptor describing the capabilities of this facility
	 */
	public Capabilities getCapabilities() {
		return capabilities;
	}
	
	/**
	 * Contribute a Component to this facility.
	 * 
	 * @param componentProducer supplies the component, given the context of the
	 * facility in question.
	 * 
	 * @return
	 */
//	public abstract Component contribute(Component component);
	
	/**
	 * Get a list of the extension points available on this Facility
	 * 
	 * @return
	 */
	public abstract Set<? extends Facility> getExtensionPoints();
	
	/**
	 * Get a list of extension points with the desired capabilities.
	 * 
	 * @param capabilities
	 * @return
	 */
	public abstract Set<? extends Facility> getExtensionPoints(Capabilities capabilities);
}
