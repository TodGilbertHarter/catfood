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
 * Hairball mainline. This gives us a stand-alone hairball parser/REPL and a class which can be instantiate to provide
 * a complete hairball 'engine'.
 * 
 * @author tharter
 *
 */
public class Hairball {
	private final Dictionary rootDictionary = new Dictionary("root");
//	private WordStream wordStream;
	private final Parser parser;
	private final Interpreter interpreter;

	public static void main(String[] args) throws IOException {
		WordStream wordStream = makeWordStream(args);
		Output output = makeOutput(args);
		Hairball hairball = new Hairball(wordStream,output);
		hairball.execute();
	}
	
	/**
	 * @param args
	 * @return
	 */
	private static Output makeOutput(String[] args) {
		return new ConsoleOutput();
	}

	/**
	 * @param args
	 * @return
	 */
	private static WordStream makeWordStream(String[] args) {
		return new ConsoleWordStream("\n>");
	}

	public Hairball(WordStream wordStream, Output output) {
		interpreter = new Interpreter();
		ParserContext pcontext = new ParserContext(wordStream,rootDictionary,interpreter,output);
		interpreter.setParserContext(pcontext);
		parser = new Parser(pcontext);
	}
	
	public void execute() throws IOException {
		parser.interpret();
	}
	
}
