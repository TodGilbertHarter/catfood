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
package com.giantelectronicbrain.catfood.hairball;

import java.io.IOException;

/**
 * API contract for an output sink.
 * 
 * @author tharter
 *
 */
public interface Output {
	
	/**
	 * Output a space character.
	 * 
	 * @throws IOException
	 */
	public void space() throws IOException;

	/**
	 * Print some text to the output.
	 * 
	 * @param output
	 * @throws IOException 
	 */
	public abstract void emit(String output) throws IOException;
}
