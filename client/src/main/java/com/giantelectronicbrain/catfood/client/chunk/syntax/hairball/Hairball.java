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
package com.giantelectronicbrain.catfood.client.chunk.syntax.hairball;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.giantelectronicbrain.catfood.client.chunk.syntax.Syntax;
import com.giantelectronicbrain.catfood.client.chunk.syntax.SyntaxRegistry;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.hairball.HairballException;
import com.giantelectronicbrain.catfood.hairball.NullOutput;
import com.giantelectronicbrain.catfood.hairball.Output;
import com.giantelectronicbrain.catfood.hairball.StreamOutput;
import com.giantelectronicbrain.catfood.hairball.StringWordStream;
import com.giantelectronicbrain.catfood.hairball.WordStream;

import elemental2.dom.Element;

/**
 * Hairball syntax handler. This parses a vocabulary found in a tag with
 * the id "htmlvocab". It will then interpret HAIRBALL chunks, making the
 * core vocabulary plus any additional definitions found in the tag, available
 * to the Interpreter.
 * 
 * @author tharter
 *
 */
public class Hairball implements Syntax {
	private static final Logger log = Client.PLATFORM.getLogger(Hairball.class.getName());

	private final com.giantelectronicbrain.catfood.hairball.Hairball hairball;
	
	public Hairball() {
		this.hairball = new com.giantelectronicbrain.catfood.hairball.Hairball();
		try {
			getVocabulary();
		} catch (IOException | HairballException e) {
			log.log(Level.WARNING,"Failed to load Hairball HTML vocabulary");
			e.printStackTrace();
		}
	}
	
	private void getVocabulary() throws IOException, HairballException {
		Output output = new NullOutput();
		Element htmlVocab = Fluent.document.getElementById("htmlvocab");
		WordStream input = new StringWordStream(htmlVocab.textContent);
		hairball.setIO(input, output);
		hairball.execute();
	}
	
	private void setUp(String inputData, ByteArrayOutputStream out) {
		Output output = new StreamOutput(out);
		WordStream input = new StringWordStream(inputData);
		hairball.setIO(input, output);
	}
	
	@Override
	public String generateHTML(Chunk chunk) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		setUp(chunk.getContent(),out);
		try {
			hairball.execute();
		} catch (IOException | HairballException e) {
			throw new RuntimeException("Hairball exception",e);
		}
		return out.toString();
	}
}
