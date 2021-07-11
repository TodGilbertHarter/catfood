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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hairball outer interpreter. The outer interpreter is responsible for interpreting
 * program input in a ParserContext and invoking the appropriate behaviors of
 * encountered words using that context. 
 * 
 * The interpreter has two modes, interpreting mode and compiling mode. In the
 * interpreting mode it simply parses words off the context's input and either
 * executes their runtime behavior if they match dictionary entries, or emits
 * them to output otherwise.
 * 
 * In compiling mode the interpreter invokes the current vocabulary's compile
 * time behavior handler, which normally compiles either the runtime or compile time
 * token for the word, depending on its type.
 * 
 * The parser can handle one or two special scenarios as well. Note that a couple
 * of its behaviors can be vectored, this is still a TBD area.
 * 
 * @author tharter
 *
 */

public class Parser {
	private static final Logger log = Hairball.PLATFORM.getLogger(Parser.class.getName());

	private boolean interpreting = true;
	private ParserContext currentContext;
	public static interface ParserBehavior {
		public abstract void handle(Word word) throws HairballException, IOException;
	}
	
	private ParserBehavior parserBehavior = this::executeWord;
	
	/**
	 * Create a parser with the given ParserContext.
	 * 
	 * @param currentContext the context to parse from.
	 */
	public Parser(ParserContext currentContext) {
		this.currentContext = currentContext;
	}
	
	/**
	 * Create an unattached parser. A context must be supplied before parsing
	 * can take place.
	 * 
	 */
	public Parser() {
		
	}
	
	/**
	 * Set the current context for this parser. Also returns the old context
	 * so that it could be restored later.
	 * 
	 * @param currentContext the new parser context
	 * @return the previous context, or null
	 */
	public ParserContext setParserContext(ParserContext currentContext) {
		ParserContext temp = this.currentContext;
		this.currentContext = currentContext;
		return temp;
	}
	
	/**
	 * Parse the current input word stream, executing the behavior provided.
	 * This will continue until the word stream is exhausted.
	 * 
	 * @param action action to perform on each token parsed
	 * @return the parser context, which is the whole VM state
	 * @throws IOException
	 * @throws HairballException 
	 */
	public ParserContext parse() throws IOException, HairballException {
		IWordStream wordStream = currentContext.getWordStream();
		Word word = wordStream.getNextWord();
		while(word != null) {
			log.log(Level.FINEST,"We got a word, "+word);
			parserBehavior.handle(word);
			word = wordStream.getNextWord();
		}
		flushLitAccum(); // make sure nothing is left behind in some edge cases
		return currentContext;
	}
	
	/**
	 * Execute the interpreting mode behavior of the outer interpreter.
	 * 
	 * @throws IOException
	 */
	public ParserContext interpret() throws IOException, HairballException {
		this.interpreting = true;
		this.parserBehavior = this::executeWord;
		return currentContext;
	}
	
	/**
	 * Execute the compiling mode behavior of the outer interpreter.
	 * 
	 * @throws IOException
	 * @throws HairballException 
	 */
	public ParserContext compile() throws IOException, HairballException {
		this.interpreting = false;
		this.parserBehavior = this::compileWord;
		return currentContext;
	}
	
	/**
	 * Execute the runtime behavior of the current word.
	 * 
	 * @param word
	 * @throws HairballException 
	 */
	public void executeWord(Word word) throws HairballException {
		Definition definition = currentContext.getDictionary().lookUp(word);
		if(definition != null) {
			flushLitAccum();
			Token runTime = definition.getRunTime();
			currentContext.getInterpreter().execute(runTime);
		} else {
			if(!isNumber(word))
				handleLiteralWord(word);
		}
	}
	
	private boolean isNumber(Word word) throws HairballException {
		return false;
/*		try {
			Integer v = Integer.valueOf(word.getValue());
			currentContext.getInterpreter().push(v);
			flushLitAccum();
			return true;
		} catch(NumberFormatException e) {
			return false;
		} */
	}
	
	/**
	 * Compile words. If the word is a literal, then add a literal token to the current definition's
	 * behavior. If the word is defined, then add its token to the current definition's behavior.
	 * 
	 * @param word
	 * @throws HairballException 
	 */
	public void compileWord(Word word) throws HairballException {
		Definition definition = currentContext.getDictionary().lookUp(word);
		if(definition != null) {
			flushLitAccum();
			Token compileTime = definition.getCompileTime();
			currentContext.getInterpreter().push(definition);
			currentContext.getInterpreter().execute(compileTime);
		} else { // this is a literal
//			Token lToken = new LiteralToken(word.getValue());
//			currentContext.getDictionary().addToken(lToken);
//			currentContext.getDictionary().addToken(emit);
			handleLiteralWord(word);
		}
	}

	private StringBuilder litAccum = new StringBuilder();
	
	private void handleLiteralWord(Word lWord) {
		if(litAccum.length() > 0) litAccum.append(' ');
		litAccum.append(lWord.getValue());
	}
	
	private void flushLitAccum() throws HairballException {
		log.log(Level.FINEST,"Calling flushLitAccum");
		
		if(litAccum.length() > 0) {
			LiteralToken token = new LiteralToken("accumLiteral",litAccum.toString());
			litAccum = new StringBuilder();
			if(interpreting) {
				token.execute(currentContext.getInterpreter());
				emit.execute(currentContext.getInterpreter());
			} else {
				currentContext.getDictionary().addToken(token);
				currentContext.getDictionary().addToken(emit);
			}
		}
	}
	
	private static final Token emit = new NativeToken("emit",(interpreter) -> {
			try {
				interpreter.getParserContext().getOutput().emit((String)interpreter.pop());
			} catch (IOException e) {
				throw new HairballException("Failed to write output",e);
			}
		});
	
	/**
	 * Get the current parser context for this parser.
	 * 
	 * @return parser context
	 */
	public ParserContext getContext() {
		return this.currentContext;
	}
}
