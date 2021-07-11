/**
 * This software is Copyright (C) 2021 Tod G. Harter. All rights reserved.
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

/**
 * Abstract implementation of a word stream which utilizes a BufferedReader as its input.
 * Concrete implementations should merely need to provide a constructor extension which initializes
 * the instance's reader.
 * 
 * @author tharter
 *
 */
public abstract class WordStream implements IWordStream {
	private static final Logger log = Hairball.PLATFORM.getLogger(WordStream.class.getName());

	private String input = "";
	private Scanner inputScanner;
	protected BufferedReader reader;

	/**
	 * Create a stream with an initially empty input. This constructor needs to be
	 * extended to provide logic to initialize the reader field.
	 * 
	 */
	public WordStream() {
		inputScanner = new Scanner(input);
	}

	/**
	 * Return the next line of input from the reader. This is exposed so that
	 * it can be overridden in certain specific cases.
	 * 
	 * @return String the next line of input
	 * @throws IOException if reading fails
	 */
	protected String getNextLine() throws IOException {
		return reader.readLine();
	}
	
	private String getNext() throws IOException {
		log.entering(this.getClass().getName(), "getNext");
		
		if(inputScanner.hasNext()) {
			log.log(Level.FINEST,"Current line has more tokens, returning next one");
			return inputScanner.next();
		} else {
			input = this.getNextLine();
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
	public Word getNextWord() throws IOException {
		log.entering(this.getClass().getName(), "getNextWord");
		
		String next = getNext();
		return next == null ? null : new Word(next);
	}

	@Override
	public String getToMatching(String match) throws IOException, IllegalArgumentException {
//System.out.println("Got to WordStream.getToMatching() with match of "+match);
		log.log(Level.FINEST,"Entering getToMatching, with match of "+match);
		
		if(match == null)
			throw new IllegalArgumentException("Cannot match against null");
		StringBuffer sb = new StringBuffer();
		String more =  getNext();
//System.out.println("more returned "+more);
		boolean first = true;
//		while(!(more.equals(match))) {
		while(!(match.equals(more)) && more != null) {
			if(!first) sb.append(' ');
			first = false;
			sb.append(more);
			more = getNext();
		}
		return sb.toString();
	}

	@Override
	public boolean hasMoreTokens() throws IOException {
//System.out.println("Got to wordstream hasMoreTokens");
		boolean isHasNext = inputScanner.hasNext();
		boolean rready = reader.ready();
//System.out.println("isHasNext is "+isHasNext+", rready is "+rready);		
		return isHasNext || rready;
	}

}
