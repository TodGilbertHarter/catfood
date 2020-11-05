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
import java.util.Stack;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.IPlatform;
import com.google.gwt.core.shared.GwtIncompatible;

/**
 * Hairball mainline. This gives us a stand-alone hairball parser/REPL and a class which can be instantiate to provide
 * a complete hairball 'engine'.
 * 
 * @author tharter
 *
 */
public class Hairball {
	public static IPlatform PLATFORM = new ServerPlatform();
	private final Dictionary rootDictionary = new Dictionary("root");
//	private WordStream wordStream;
	private final Parser parser;
	private final Interpreter interpreter;

	@GwtIncompatible
	public static void main(String[] args) throws IOException, HairballException {
		WordStream wordStream = makeWordStream(args);
		Output output = makeOutput(args);
		Hairball hairball = new Hairball(wordStream,output);
		hairball.execute();
	}
	
	static class ServerPlatform implements IPlatform {

		@Override
		public boolean isClient() {
			return false;
		}

		@Override
		public Logger getLogger(String name) {
			return Logger.getLogger(name);
		}
		
	}
	
	/**
	 * @param args
	 * @return
	 */
	@GwtIncompatible
	private static Output makeOutput(String[] args) {
		return new ConsoleOutput();
	}

	/**
	 * @param args
	 * @return
	 */
	@GwtIncompatible
	private static WordStream makeWordStream(String[] args) {
		return new ConsoleWordStream("\n>");
	}

	public Hairball(WordStream wordStream, Output output) {
		this();
		setIO(wordStream, output);
	}

	public Hairball() {
		IVocabulary hbVocab = HairballVocabulary.create();
		rootDictionary.add(hbVocab);
		interpreter = new Interpreter();
		parser = new Parser();
	}
	
	public void setIO(WordStream wordStream,Output output) {
		ParserContext pcontext = new ParserContext(wordStream,rootDictionary,interpreter,output,parser);
		interpreter.setParserContext(pcontext);
		parser.setParserContext(pcontext);
	}
	
	public void setInput(WordStream wordStream) {
		Output output = this.parser.getContext().getOutput();
		ParserContext pcontext = new ParserContext(wordStream,rootDictionary,interpreter,output,parser);
		interpreter.setParserContext(pcontext);
		parser.setParserContext(pcontext);
	}
	
	/**
	 * Run the Hairball engine, processing the input until eof and generating
	 * output, etc. This is the main entry point for actually running a Hairball
	 * program.
	 * 
	 * @return
	 * @throws IOException
	 * @throws HairballException 
	 */
	public ParserContext execute() throws IOException, HairballException {
		parser.interpret();
		return parser.parse();
	}

	/**
	 * Get the whole parameter stack. This is mainly useful for testing.
	 */
	public Stack getParamStack() {
		return interpreter.getParameterStack();
	}

	/**
	 * Get the whole return stack. This is mainly useful for testing.
	 */
	public Stack getReturnStack() {
		return interpreter.getReturnStack();
	}

	/**
	 * Get the current interpreter context. Mostly useful for testing.
	 */
	public Context getInterpreterContext() {
		return interpreter.currentContext();
	}

	/**
	 * Return the parser for this Hairball instance.
	 * 
	 * @return the parser
	 */
	public Parser getParser() {
		return parser;
	}
	
}
