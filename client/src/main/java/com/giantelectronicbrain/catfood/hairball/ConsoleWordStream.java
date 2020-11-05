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
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import com.google.gwt.core.shared.GwtIncompatible;

/**
 * A word stream which is attached to the console.
 * 
 * @author tharter
 *
 */
@GwtIncompatible
public class ConsoleWordStream implements WordStream {
//	private final Console console;
	private final String prompt;
	private String input = "";
	private Scanner inputScanner;
	private BufferedReader reader;
	private OutputStream out;
	private byte[] pBytes = null;
	
	/**
	 * Create a ConsoleWordStream attached to STDIN/OUT.
	 * 
	 * @param prompt
	 */
	public ConsoleWordStream(String prompt) {
		this.prompt = prompt;
		pBytes = prompt.getBytes(Charset.defaultCharset());
		inputScanner = new Scanner(input);
		reader = new BufferedReader(new InputStreamReader(System.in));
		out = System.out;
	}

	/**
	 * Create a ConsoleWordStream attached to the given input and output.
	 * 
	 * @param prompt
	 * @param out
	 * @param in
	 */
	public ConsoleWordStream(String prompt, OutputStream out, InputStream in) {
		this.prompt = prompt;
		pBytes = prompt.getBytes(Charset.defaultCharset());
		inputScanner = new Scanner(input);
		reader = new BufferedReader(new InputStreamReader(in));
		this.out = out;
	}

	@Override
	public Word getNextWord() throws IOException {
		String next = getNext();
		return next == null ? null : new Word(next);
	}
	
	private String getNext() throws IOException {
		if(inputScanner.hasNext()) {
			return inputScanner.next();
		} else {
			out.write(pBytes);
			input = reader.readLine();
			//TODO: this issue is kind of a corner case, not sure how to handle it
			if(input == null) return null; // throw new IOException("Console ran out of input!");
			inputScanner = new Scanner(input);
			if(inputScanner.hasNext())
				return inputScanner.next();
			return "\n\n"; // we got double returns, which is a special token for us
		}
	}

	@Override
	public boolean hasMoreTokens() {
		return true;
	}

	@Override
	public String getToMatching(String match) throws IOException {
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
