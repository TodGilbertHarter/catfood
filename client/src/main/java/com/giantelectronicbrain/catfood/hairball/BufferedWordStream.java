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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GwtIncompatible;

/**
 * A word stream which handles non-interactive input. 
 * 
 * @author tharter
 *
 */
@GwtIncompatible
public class BufferedWordStream implements WordStream {
	private static final Logger log = Hairball.PLATFORM.getLogger(BufferedWordStream.class.getName());

	private String input = "";
	private Scanner inputScanner;
	private BufferedReader reader;
	
	/**
	 * Create a stream with the given input stream as its source.
	 * 
	 * @param prompt
	 */
	public BufferedWordStream(InputStream in) {
		inputScanner = new Scanner(input);
		reader = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public Word getNextWord() throws IOException {
		log.entering(this.getClass().getName(), "getNextWord");
		
		String next = getNext();
		return next == null ? null : new Word(next);
	}
	
	private String getNext() throws IOException {
		log.entering(this.getClass().getName(), "getNext");
		
		if(inputScanner.hasNext()) {
			log.log(Level.FINEST,"Current line has more tokens, returning next one");
			return inputScanner.next();
		} else {
			input = reader.readLine();
			log.log(Level.FINEST,"Line was exhausted, got a new line");
			
			if(input != null) {
				inputScanner = new Scanner(input);
				if(inputScanner.hasNext()) {
					log.log(Level.FINEST,"Returning a token from the new line");
					return inputScanner.next();
				}
				log.log(Level.FINEST,"Line was exhausted, next line was blank, returning double newline");
				return "\n\n"; // we got double returns, which is a special token for us
			} else {
				log.log(Level.FINER,"No more input from word stream, returning null");
				return null; // input is exhausted.
			}
		}
	}

	@Override
	public boolean hasMoreTokens() throws IOException {
		return reader.ready();
	}

	@Override
	public String getToMatching(String match) throws IOException {
		log.log(Level.FINEST,"Entering getToMatching, with match of "+match);
		
		StringBuffer sb = new StringBuffer();
		String more =  getNext();
		boolean first = true;
		while(!(more.equals(match))) {
			if(!first) sb.append(' ');
			first = false;
			sb.append(more);
			more = getNext();
		}
		return sb.toString();
	}

}
