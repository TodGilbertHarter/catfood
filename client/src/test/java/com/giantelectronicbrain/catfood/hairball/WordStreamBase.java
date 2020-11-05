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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

/**
 * @author tharter
 *
 */
public abstract class WordStreamBase {
	InputStream in;
	OutputStream out;
	
	public abstract WordStream setUp(String input);

	@Test
	public void handleEmptyString() throws IOException {
		WordStream uut = setUp("");
		Word result = uut.getNextWord();
		assertEquals(null,result);
	}
	
	@Test
	public void handleNewLines() throws IOException {
		WordStream uut = setUp("\n\n");
		Word result = uut.getNextWord();
		assertEquals("\n\n",result.getValue());
	}
	
	@Test
	public void getWorks() throws IOException {
		WordStream uut = setUp("there are some words here");
		
		Word result = uut.getNextWord();
		assertEquals("there",result.getValue());
		result = uut.getNextWord();
		assertEquals("are",result.getValue());
		result = uut.getNextWord();
		assertEquals("some",result.getValue());
		result = uut.getNextWord();
		assertEquals("words",result.getValue());
		result = uut.getNextWord();
		assertEquals("here",result.getValue());
		result = uut.getNextWord();
		assertNull(result);
	}
	
	@Test
	public void testMultipleSpaces() throws IOException {
		 WordStream uut = setUp("this   word");
		 Word result1 = uut.getNextWord();
		 Word result2 = uut.getNextWord();
		 assertEquals("this",result1.getValue());
		 assertEquals("word",result2.getValue());
	}
	
	@Test
	public void testLeadingSpaces() throws IOException {
		 WordStream uut = setUp("    this word");
		 Word result1 = uut.getNextWord();
		 Word result2 = uut.getNextWord();
		 assertEquals("this",result1.getValue());
		 assertEquals("word",result2.getValue());
	}
	
	@Test
	public void testLeadingTabs() throws IOException {
		 WordStream uut = setUp("	this	word");
		 Word result1 = uut.getNextWord();
		 Word result2 = uut.getNextWord();
		 assertEquals("this",result1.getValue());
		 assertEquals("word",result2.getValue());
	}
	
	@Test
	public void getToMatchingWorks() throws IOException {
		 WordStream uut = setUp("this is going to be some fun stuff");
		 String result = uut.getToMatching("fun");
		 assertEquals("this is going to be some",result);
	}

	@Test
	public void emptyLineReturnsDoubleNewline() throws IOException {
		WordStream uut = setUp("hi \n\nthere!");
		Word first = uut.getNextWord();
		Word second = uut.getNextWord();
		Word third = uut.getNextWord();
		
		assertEquals("hi",first.getValue());
		assertEquals("\n\n",second.getValue());
		assertEquals("there!",third.getValue());
	}
}
