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
import java.util.List;

/**
 * Define the core 'native' word set.
 * 
 * @author tharter
 *
 */
public class HairballVocabulary {
	
	public static IVocabulary create() {
		IVocabulary hbVocab = new Vocabulary("HAIRBALL");
		
		return hbVocab;
	}
	
	private static final List<Definition> defList = new ArrayList<>();
	{
		/**
		 * Default compile time behavior for words, take the runtime
		 * behavior token and insert it into the current definition's
		 * token list. Which list that will be is determined by the
		 * mode, DOER or DOES.
		 */
		Token compile = new NativeToken((interpreter) -> {
			
		});

		Token emit = new NativeToken((interpreter) -> {
				interpreter.getParserContext().getOutput().emit((String)interpreter.pop());
			});
		/**
		 * Emit TOS to the output.
		 */
		defList.add(new Definition(new Word("/."),null,emit));

		
		defList.add(new Definition(new Word("/:"), null, emit));
		defList.add(new Definition(new Word(":/"), null, emit));
	}

}
