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
 * Interface contract for a stream of words. This is generally used as the input to a
 * parser, but it could also have other uses...
 * 
 * @author tharter
 *
 */
public interface WordStream {

	/**
	 * Get the next hairball Word in the input stream.
	 * 
	 * @return the next word
	 * @throws IOException if there is an input failure
	 */
	public abstract Word getNextWord() throws IOException;

	/**
	 * Consume the input up to and including the next instance of a specific
	 * hairball word which matches the input value. All the consumed input up
	 * to but not including the matched word is returned as a string.
	 * 
	 * WordStream implementations are free to block if they wish, this is
	 * synchronous input.
	 * 
	 * @param match the word value to match against
	 * @return all input up to but not including the matched value
	 * @throws IOException if there is an input failure
	 */
	public abstract String getToMatching(String match) throws IOException;
	
	/**
	 * Return true if the input stream is not yet exhausted. Interactive streams
	 * normally always return true, even if no input is currently available (IE a
	 * console where the user could input more data at a later time).
	 * 
	 * @return true if the stream can return more input at some point.
	 * @throws IOException 
	 */
	public abstract boolean hasMoreTokens() throws IOException;

}
