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
package com.giantelectronicbrain.catfood.hairball.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.giantelectronicbrain.catfood.hairball.Definition;
import com.giantelectronicbrain.catfood.hairball.HairballException;
import com.giantelectronicbrain.catfood.hairball.IVocabulary;
import com.giantelectronicbrain.catfood.hairball.NativeToken;
import com.giantelectronicbrain.catfood.hairball.ParserLocation;
import com.giantelectronicbrain.catfood.hairball.Token;
import com.giantelectronicbrain.catfood.hairball.Vocabulary;
import com.giantelectronicbrain.catfood.hairball.Word;
import com.giantelectronicbrain.catfood.hairball.tokens.Compile;

/**
 * Core vocabulary set for PDF generation. This is enough logic to allow us to
 * write most of the syntax in Hairball. The idea is to be able to support all
 * the main functionality available in other outputs such as HTML or MD.
 * 
 * @author tharter
 *
 */
public class PdfVocabulary {
	
	/**
	 * Static factory to create the one and only needed instance of this class.
	 * This is how PdfVocabulary should always be created, there is no need
	 * for more than one.
	 * 
	 * @return
	 */
	public static IVocabulary create() {
		IVocabulary hbVocab = new Vocabulary("PDF");
		for(Definition def : defList) {
			hbVocab.add(def);
		}
		return hbVocab;
	}
	
	/**
	 * The actual definitions which will be placed within the vocabulary.
	 */
	private static final List<Definition> defList = new ArrayList<>();
	static {
		
		Token document = new NativeToken("Document", (interpreter) -> {
			PDDocument doc = new PDDocument();
			interpreter.push(doc);
			return true;
		});
		defList.add(new Definition(new Word("/DOCUMENT\""),Compile.INSTANCE,document));

		Token closeDocument = new NativeToken("closeDocument",(interpreter) -> {
			ParserLocation pl = new ParserLocation(interpreter.getParserContext().getWordStream());
			PDDocument doc = (PDDocument) interpreter.pop();
			try {
				doc.close();
			} catch (IOException e) {
				throw new HairballException(pl.makeErrorMessage("Cannot close PDF Document"),e);
			}
			return true;
		});
		defList.add(new Definition(new Word("DOCUMENT/"),Compile.INSTANCE,closeDocument));
	}
}
