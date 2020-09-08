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

import com.google.gwt.core.client.GWT;

/**
 * GWT implementation of IPlatform. I'm not sure how to make these 'pluggable' so we
 * could just flip a switch and implement another one with say TeaVM or J2CL and have
 * it 'just work', but at least the ugly is here.
 * 
 * @author tharter
 *
 */
public class GWTPlatform implements IPlatform {

	@Override
	public boolean isClient() {
		return GWT.isClient();
	}

}
