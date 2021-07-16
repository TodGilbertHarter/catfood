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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.conf.ConfigurationException;

import io.vertx.core.Vertx;

//import com.giantelectronicbrain.catfood.client.IPlatform;
//import com.google.gwt.core.shared.GwtIncompatible;

/**
 * Hairball mainline. This gives us a stand-alone hairball parser/REPL and a class which can be instantiate to provide
 * a complete hairball 'engine'.
 * 
 * @author tharter
 *
 */
public class Hairball {
	public static ServerPlatform PLATFORM = new ServerPlatform();
	private final Dictionary rootDictionary = new Dictionary("root");
	private final Parser parser;
	private final Interpreter interpreter;

//	@GwtIncompatible
	public static void main(String[] args) throws IOException, HairballException, ConfigurationException {
		Vertx vertx = Vertx.vertx();
		try {
			Object[] conf = Configurator.createConfiguration(Arrays.asList(args));
			Properties configuration = (Properties) conf[0];
			List<String> argList = (List<String>) conf[1];
			IWordStream wordStream = makeWordStream(vertx, argList, configuration);
			Output output = makeOutput(configuration);
			Hairball hairball = new Hairball(wordStream,output);
			hairball.execute();
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		vertx.close();
	}
	
	static class ServerPlatform { //implements IPlatform {

//		@Override
		public boolean isClient() {
			return false;
		}

//		@Override
		public Logger getLogger(String name) {
			return Logger.getLogger(name);
		}
		
	}
	
	/**
	 * Create a console output. This ignores args.
	 * 
	 * @param args
	 * @return
	 */
//	@GwtIncompatible
	private static Output makeOutput(Properties properties) {
		//TODO: support directing output to other places besides STDOUT
		return new ConsoleOutput();
	}

	/**
	 * Create a WordStream which uses the current working directory and a list
	 * of arguments. If the argument list is empty, it will be a ConsoleWordStream,
	 * else a FileCollectionWordStream.
	 * 
	 * @param args
	 * @return
	 */
//	@GwtIncompatible
	private static IWordStream makeWordStream(Vertx vertx, List<String> args, Properties properties) {
		if(args == null || args.size() == 0)
			return new ConsoleWordStream("\n>");
		else {
			String cwd = (String) properties.get("base");
			if(cwd == null) cwd = ".";
			return new FileCollectionWordStream(vertx,"",args,cwd);
		}
	}

	/**
	 * Create a Hairball instance with the given input and output sources.
	 * 
	 * @param wordStream IWordStream where input comes from
	 * @param output Output where we send output.
	 */
	public Hairball(IWordStream wordStream, Output output) {
		this();
		setIO(wordStream, output);
	}

	/**
	 * Create a Hairball instance. This doesn't have any associated output stream or
	 * input IWordStream. Those will have to be supplied by a call to setIO.
	 */
	public Hairball() {
		IVocabulary hbVocab = HairballVocabulary.create();
		rootDictionary.add(hbVocab);
		interpreter = new Interpreter();
		parser = new Parser();
	}
	
	/**
	 * Set the input and output of this Hairball instance.
	 * 
	 * @param wordStream
	 * @param output
	 */
	public void setIO(IWordStream wordStream,Output output) {
		ParserContext pcontext = new ParserContext(wordStream,rootDictionary,interpreter,output,parser);
		interpreter.setParserContext(pcontext);
		parser.setParserContext(pcontext);
	}
	
	/**
	 * Set the input for this instance, the output is left unchanged.
	 * 
	 * @param wordStream
	 */
	public void setInput(IWordStream wordStream) {
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
	 * @return the ParserContext
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
