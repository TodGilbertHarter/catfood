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
import java.util.function.Consumer;

/**
 * Hairball outer interpreter.
 * 
 * @author tharter
 *
 */

public class Parser {
//	private boolean interpreting = true;
	private ParserContext currentContext;

	public Parser(ParserContext currentContext) {
		this.currentContext = currentContext;
	}
	
	public void parse(Consumer<Word> action) throws IOException {
		WordStream wordStream = currentContext.getWordStream();
		do {
			Word token = wordStream.getNextToken();
			action.accept(token);
		} while(wordStream.hasMoreTokens());
	}
	
	public void interpret() throws IOException {
		parse(this::executeToken);
	}
	
	public void compile() throws IOException {
		parse(this::compileToken);
	}
	
	public void executeToken(Word word) {
		Definition definition = currentContext.getDictionary().lookUp(word);
		if(definition != null) {
			Token runTime = definition.getRunTime();
			currentContext.getInterpreter().execute(runTime);
		} else {
			literal(word);
		}
	}
	
	public void literal(Word word) {
		currentContext.getOutput().emit(word.getValue());
	}
	
	public void compileToken(Word token) {
		throw new UnsupportedOperationException("we can't do this yet");
	}
}
