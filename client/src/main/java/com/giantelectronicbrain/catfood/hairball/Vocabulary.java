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

import java.util.HashMap;
import java.util.Map;

/**
 * A vocabulary is a collection of hairball words.
 * 
 * @author tharter
 *
 */
public class Vocabulary implements IVocabulary {
	private final String name;
	private final Map<Word,Definition> definitions = new HashMap<>(); 
	
	public Vocabulary(String name) {
		this.name = name;
	}

	@Override
	public Definition lookUp(Word word) {
		return definitions.get(word);
	}
	
	/**
	 * Add a new definition to the vocabulary. If there is an
	 * existing definition assigned to this token, it is forgotten.
	 * 
	 * @param newDefinition
	 */
	public void add(Definition newDefinition) {
		this.definitions.put(newDefinition.getName(), newDefinition);
	}
	
}
