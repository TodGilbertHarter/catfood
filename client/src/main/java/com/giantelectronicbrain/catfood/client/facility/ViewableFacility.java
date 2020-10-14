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

import java.util.function.Function;

import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.giantelectronicbrain.catfood.client.fluent.Viewable;

/**
 * Viewable Facilities have an associated UI component of some sort.
 * 
 * @author tharter
 *
 */
public abstract class ViewableFacility extends Facility implements ViewableComponent {
	protected ViewOn<ViewableFacility> viewOn;

	/**
	 * @param capabilities
	 */
	protected ViewableFacility(Capabilities capabilities) {
		super(capabilities);
	}

	public Viewable getDisplay() {
		return viewOn;
	}
	
	/**
	 * Return the fluent which will be the parent of any UI
	 * contributions to this facility. Subclasses may want to
	 * override this, if for instance contributions should not
	 * be linked directly to this facilities top-level fluent.
	 * 
	 * @return a Fluent which will be the parent of a contribution
	 */
	public Fluent getUiContributionPoint() {
		//NOTE: it might be better if this accepted some indicator of what the contribution is?
		return viewOn.getView();
	}
	
	protected abstract Function<ViewableFacility, Fluent> getRenderFunction();

}
