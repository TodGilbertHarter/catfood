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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * A dictionary is a vocabulary of vocabularies, which will be searched in inverse
 * order of addition, newest to oldest.
 * 
 * A dictionary has the concept of the 'active' vocabulary. It will accept new definitions
 * and add them to the active vocabulary. It also has a concept of a 'current' definition, which
 * is simply a compiling state which will be used to construct a new definition when it is finalized.
 * The new definition will be added to the active vocabulary.
 * 
 * Vocabularies can be added and removed from the dictionary, which always operates in a LIFO fashion.
 * When a new vocabulary is added, it goes to the top of the 'stack', where it is always searched first
 * for definitions. The top of the stack can be discarded, or a specific vocabulary and all newer ones
 * can be removed. 
 * 
 * Removed vocabularies are still available to be re-added later. The vocabulary management words will
 * perform this task.
 * 
 * @author tharter
 *
 */
public class Dictionary implements IVocabulary {
	private final String name;
	private final Stack<IVocabulary> vocabularies = new Stack<>();
	private EmptyDefinition currentDefinition = new EmptyDefinition();
	private Consumer<Token> doerDoes = this::addToRuntime;
	private boolean doer = true;

	private class EmptyDefinition {
		Word name;
		List<Token> compileTime = new LinkedList<>();
		List<Token> runTime = new LinkedList<>();
		
		public void addCompileToken(Token newToken) {
			this.compileTime.add(newToken);
		}
		
		public void addRuntimeToken(Token newToken) {
			this.runTime.add(newToken);
		}
		
	}

	/**
	 * Create a new dictionary with the given name.
	 * 
	 * @param name
	 */
	public Dictionary(String name) {
		this.name = name;
	}

	/**
	 * Set the dictionary compilation state to 'doer'. This will cause addToken to add tokens to the
	 * compile time behavior of the current definition.
	 */
	public void doer() {
		doerDoes = this::addToCompileTime;
		doer = true;
	}

	/**
	 * Set the dictionary compilation state to 'does'. This will cause addToken to add tokens to the
	 * runtime behavior of the current definition.
	 */
	public void does() {
		doerDoes = this::addToRuntime;
		doer = false;
	}

	/**
	 * Probe the dictionary compiling mode. If the mode is doer, return true, otherwise false.
	 * 
	 * @return true if doer, false if does
	 */
	public boolean isDoer() {
		return doer;
	}
	
	/**
	 * Insert a token into the current definition. The 'doer/does' state of the dictionary will determine
	 * which behavior is targeted.
	 * 
	 * @param token
	 */
	public void addToken(Token token) {
		doerDoes.accept(token);
	}
	
	/**
	 * Initiate the creation of a new definition.
	 * 
	 * @param name
	 */
	public void create(Word name) {
		currentDefinition.name = name;
	}

	private void addToCompileTime(Token token) {
		currentDefinition.addCompileToken(token);
	}
	
	private void addToRuntime(Token token) {
		currentDefinition.addRuntimeToken(token);
	}
	
	/**
	 * Close out the current definition and add it to the active vocabulary.
	 */
	public void define() {
		List<Token> rtList = new ArrayList<>();
		rtList.addAll(currentDefinition.runTime);
		
		List<Token> ctList = new ArrayList<>();
		ctList.addAll(currentDefinition.compileTime);
		
		Definition def = new Definition(
				currentDefinition.name,
				new InterpreterToken(ctList),
				new InterpreterToken(rtList)
				);

		this.add(def);
	}
	
	/**
	 * Add a vocabulary to this dictionary.
	 * 
	 * @param vocabulary
	 */
	public void add(IVocabulary vocabulary) {
		vocabularies.push(vocabulary);
	}
	
	/**
	 * Remove the most recently added vocabulary.
	 * 
	 */
	public void remove() {
		vocabularies.pop();
	}

	/**
	 * Pop the given vocabulary and all more recently added vocabularies from
	 * this dictionary.
	 * 
	 * @param vocabulary the vocabulary to remove
	 */
	public void remove(IVocabulary vocabulary) {
		while(vocabularies.size() != 0) {
			IVocabulary popped = vocabularies.pop();
			if(popped.equals(vocabulary)) return;
		}
	}
	
	@Override
	public Definition lookUp(Word word) {
		for(IVocabulary vocabulary : vocabularies) {
			Definition def = vocabulary.lookUp(word);
			if(def != null) return def;
		}
		return null;
	}

	@Override
	public void add(Definition def) {
		IVocabulary activeVocab = vocabularies.peek();
		activeVocab.add(def);
	}
}
