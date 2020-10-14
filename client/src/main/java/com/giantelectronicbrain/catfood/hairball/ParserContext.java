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

/**
 * @author tharter
 *
 */
public class ParserContext {
	private WordStream wordStream;
	private Dictionary dictionary;
	private Interpreter interpreter;
	private Output output;
	
	/**
	 * @param wordStream
	 * @param rootDictionary
	 */
	public ParserContext(WordStream wordStream, Dictionary rootDictionary, Interpreter interpreter, Output output) {
		this.wordStream = wordStream;
		this.dictionary = rootDictionary;
		this.interpreter = interpreter;
		this.output = output;
	}

	/**
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(Output output) {
		this.output = output;
	}

	/**
	 * @return the wordStream
	 */
	public WordStream getWordStream() {
		return wordStream;
	}

	/**
	 * @param wordStream the wordStream to set
	 */
	public void setWordStream(WordStream wordStream) {
		this.wordStream = wordStream;
	}

	/**
	 * @return the dictionary
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}

	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * @return the interpreter
	 */
	public Interpreter getInterpreter() {
		return interpreter;
	}

	/**
	 * @param interpreter the interpreter to set
	 */
	public void setInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}


}
