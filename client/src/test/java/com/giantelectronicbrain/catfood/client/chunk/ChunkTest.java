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
package com.giantelectronicbrain.catfood.client.chunk;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;

/**
 * Test Chunk serialization and deserialization.
 * 
 * @author tharter
 *
 */
public class ChunkTest extends GWTTestCase {
	
	public interface TestChunkMapper extends ObjectMapper<Chunk> {
		TestChunkMapper INSTANCE = GWT.create(TestChunkMapper.class);
	}

	public interface TestChunkIdMapper extends ObjectMapper<ChunkId> {
		TestChunkIdMapper INSTANCE = GWT.create(TestChunkIdMapper.class);
	}
	
	@Test
	public void testCssAdditionAndRemoval() {
		Fluent div = Fluent.body.div().css(Css.display, "none", Css.color, "red");
		String style = div.att(Att.style);
		assertEquals("display: none",style);
		div.css(Css.display,"block");
		style = div.att(Att.style);
		assertEquals("display: block",style);
		div.css(Css.display,"none");
		style = div.att(Att.style);
		assertEquals("display: none",style);
	}
	
	@Test
	public void testSerialization() {
		ChunkId chunkId = new ChunkId("idvalue");
		Chunk chunk = new Chunk("the content", chunkId, "topic name", Language.MARKDOWN);
		String json = TestChunkMapper.INSTANCE.write(chunk);
		assertNotNull(json);
		String expected = "{\"content\":\"the content\",\"name\":\"topic name\",\"lang\":\"MARKDOWN\",\"@rid\":\"idvalue\"}";
		assertEquals(expected,json);
	}

	@Test
	public void testDeserialization() {
		ChunkId chunkIdExpected = new ChunkId("idvalue");
		Chunk chunkExpected = new Chunk("the content", chunkIdExpected, "topic name", Language.MARKDOWN);
		String json = "{\"content\":\"the content\",\"name\":\"topic name\",\"lang\":\"MARKDOWN\",\"@rid\":\"#idvalue\"}";
		Chunk result = TestChunkMapper.INSTANCE.read(json);
		assertNotNull(result);
		assertEquals(chunkExpected,result);
	}
	
	@Test
	public void testChunkIdDeserialization() {
		ChunkId chunkIdExpected = new ChunkId("idvalue");
		String json = "{\"chunkId\":\"idvalue\"}";
		ChunkId result = TestChunkIdMapper.INSTANCE.read(json);
		assertNotNull(result);
		assertEquals(chunkIdExpected,result);
	}
	
	@Test
	public void testChunkIdSerialization() {
		ChunkId chunkId = new ChunkId("idvalue");
		String json = TestChunkIdMapper.INSTANCE.write(chunkId);
		assertNotNull(json);
		String expected = "{\"chunkId\":\"idvalue\"}";
		assertEquals(expected,json);
	}
	
//	@Override
	public String getModuleName() {
		return "a";
	}
	
}
