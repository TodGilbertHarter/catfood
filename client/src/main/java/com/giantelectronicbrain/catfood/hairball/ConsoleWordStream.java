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
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * A word stream which is attached to the console.
 * 
 * @author tharter
 *
 */
public class ConsoleWordStream implements WordStream {
//	private final Console console;
	private final String prompt;
	private String input = "";
	private Scanner inputScanner;
	private BufferedReader reader;

	public ConsoleWordStream(String prompt) {
		this.prompt = prompt;
		inputScanner = new Scanner(input);
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public Word getNextToken() throws IOException {
		if(inputScanner.hasNext()) {
			return new Word(inputScanner.next());
		} else {
			System.out.print(prompt);
			input = reader.readLine();
			inputScanner = new Scanner(input);
			if(inputScanner.hasNext())
				return new Word(inputScanner.next());
			return new Word("\n\n"); // we got double returns, which is a special token for us
		}
	}

	@Override
	public boolean hasMoreTokens() {
		return true;
	}

}
