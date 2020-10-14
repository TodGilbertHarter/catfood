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
 * This is a factory which creates the HTML
 * output vocabulary for Hairball.
 * 
 * @author tharter
 *
 */
public class HTMLVocabulary {

	public static IVocabulary create() {
		IVocabulary htmlVocab = new Vocabulary("HTML");
		
		return htmlVocab;
	}
	
	private static final List<Definition> defList = new ArrayList<>();
	{
		Token emit = new NativeToken((interpreter) -> {
				interpreter.getParserContext().getOutput().emit((String)interpreter.pop());
			});
		defList.add(new Definition(new Word("/P"), null, emit));
		defList.add(new Definition(new Word("P/"), null, emit));
	}
}
