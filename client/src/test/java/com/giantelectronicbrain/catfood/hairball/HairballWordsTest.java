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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.util.Stack;

import org.junit.Test;

/**
 * Tests of the words in the Hairball core vocabulary.
 * 
 * @author tharter
 *
 */
public class HairballWordsTest {

	@Test
	public void testNoop() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("//",out);
		ParserContext ctx = uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		Context ictx = uut.getInterpreterContext();
		assertNull(ictx);
		assertNotNull(ctx);
		String output = out.toString();
		assertTrue(output.isEmpty());
	}

	@Test
	public void testHereStore() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/HERE!",out);

		Stack<Object> pStack = uut.getParamStack();
		LiteralToken literalToken = new LiteralToken("literal","this is a literal");
		pStack.push(literalToken);

		ParserContext ctx = uut.getParser().getContext();
		ctx.getDictionary().create(new Word("TEST"));
		ctx.getDictionary().does();
				
		uut.execute();
		ctx.getDictionary().define();
		
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Definition def = ctx.getDictionary().lookUp(new Word("TEST"));
		assertNotNull(def);
		def.getRunTime().execute(ctx.getInterpreter());
		
		Definition dot = ctx.getDictionary().lookUp(new Word("/."));
		dot.getRunTime().execute(ctx.getInterpreter());
		
		assertEquals("this is a literal",out.toString());
	}
	
	@Test
	public void testStore() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/HERE! /!",out);

		Stack<Object> pStack = uut.getParamStack();
		LiteralToken l2 = new LiteralToken("another","another literal");
		pStack.push(l2);
		pStack.push(Integer.valueOf(0));
		LiteralToken literalToken = new LiteralToken("literal","this is a literal");
		pStack.push(literalToken);

		ParserContext ctx = uut.getParser().getContext();
		ctx.getDictionary().create(new Word("TEST"));
		ctx.getDictionary().does();
				
		uut.execute();
		ctx.getDictionary().define();

		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Definition def = ctx.getDictionary().lookUp(new Word("TEST"));
		assertNotNull(def);
		def.getRunTime().execute(ctx.getInterpreter());
		
		Definition dot = ctx.getDictionary().lookUp(new Word("/."));
		dot.getRunTime().execute(ctx.getInterpreter());
		
		assertEquals("another literal",out.toString());
	}
	
	@Test
	public void testExecute() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/EXECUTE",out);

		Token token = new LiteralToken("42",42);
		Stack<Object> pStack = uut.getParamStack();
		pStack.push(token);
		
		uut.execute();
		assertEquals(1,pStack.size());
		assertEquals(42,pStack.pop());
		
	}
	
	@Test
	public void testDot() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/.",out);
		Stack<Object> pStack = uut.getParamStack();
		String literal = "this is a literal";
		pStack.push(literal);

		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals(literal,output);
	}
	
	@Test
	public void testW() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/W stuff",out);
		
		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(1,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Word tos = (Word) pStack.pop();
		assertNotNull(tos);
		assertEquals("stuff",tos.getValue());
	}
	
	@Test
	public void testSpace() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/SPACE",out);

		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals(" ",output);
	}
	
	@Test
	public void testCompileSpace()  throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/: /EM /SPACE <em> :/ /: EM/ </em> :/ TEST /EM TEST EM/ TEST",out);

		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals("TEST <em>TEST</em>TEST",output);
	}
	
	@Test
	public void testDotQuote() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/.\" some fun stuff \"/",out);

		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals("some fun stuff",output);
	}
	
	@Test
	public void testColon() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/: TEST some fun stuff :/\n",out);
		
		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Definition def = ctx.getDictionary().lookUp(new Word("TEST"));
		assertNotNull(def);
		def.getRunTime().execute(ctx.getInterpreter());
		assertEquals(0,pStack.size());
		rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals("some fun stuff",output);
	}
	
	@Test
	public void testAbort() {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/\" some words \"/ /ABORT",out);
		try {
			uut.execute();
			fail("must throw error");
		} catch (IOException e) {
			e.printStackTrace();
			fail("should not throw this");
		} catch (HairballException e) {
			String output = e.getMessage();
			assertEquals("some words",output);
		}
	}
	
	@Test
	public void doesHairballWork() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		Hairball uut = WordUtilities.setUp("/: /EM <em> :/\n"
				+ "/: EM/ </em> :/",out);
		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());

		String output = out.toString();
		assertEquals("",output);		

		String code = "/EM this is a test EM/";
		InputStream in = new StringBufferInputStream(code);
		WordStream wordStream = new BufferedWordStream(in);
		uut.setInput(wordStream);
		uut.execute();
		output = out.toString();
		assertEquals("<em>this is a test</em>",output);
		
	}
}
