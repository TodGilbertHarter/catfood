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

import java.util.function.BiConsumer;

import com.giantelectronicbrain.catfood.client.Client;
import com.github.nmorel.gwtjackson.client.ObjectMapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * Async store and fetch for Chunks. This should only need to be
 * instantiated once, but I'm not sure about the semantics of 
 * trying to create client-side singletons.
 * 
 * @author tharter
 *
 */
public class Repository {

	private final String GET_BYID_CHUNK_PORT = "http://localhost:8080/data/chunk/byid";
	private final String GET_BYNAME_CHUNK_PORT = "http://localhost:8080/data/chunk/byname";

	/**
	 * Get a chunk, given a chunk id.
	 * 
	 * @param id
	 * @param handler
	 */
	public void getChunk(ChunkId id, BiConsumer<Integer,Chunk> handler) {
		String url = GET_BYID_CHUNK_PORT + "/" + id.getChunkId();
		getObject(CHUNK_MAP,url,handler);
	}

	/**
	 * Get a chunk, given a topic name.
	 * 
	 * @param topic
	 * @param handler
	 */
	public void findChunkByName(String name, BiConsumer<Integer, Chunk> handler) {
		String url = GET_BYNAME_CHUNK_PORT + "/" + name;
		getObject(CHUNK_MAP,url.toString(),handler);
	}
	
	/**
	 * Call a URL, map the response to an object with the given mapper, and call the given handler to
	 * handle it.
	 * 
	 * @param <O>
	 * @param mapper
	 * @param query
	 * @param handler
	 */
	private <O> void getObject(ObjectMapper<O> mapper, String query, BiConsumer<Integer,O> handler) {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(a -> {
			if (handler == null || xhr.getReadyState() != 4) {
				return;
			}
			O result = null;
			if (xhr.getStatus() == 200) {
				result = out(xhr.getResponseText(), mapper);
			}
			handler.accept(xhr.getStatus(), result);
		});
		xhr.open("GET", query);
		xhr.send();
	}
		

	private static <O> O out(String message, ObjectMapper<O> outMapper) {
		if (message == null || outMapper == null) {
			return null;
		} else {
			return outMapper.read(message);
		}
	}

	// NOTE: this whole 'mapper' thing is a bit of ugly pollution, although perhaps we can patch it up at some point not to leak GWT.
	public static final ObjectMapper<Chunk> CHUNK_MAP = Client.PLATFORM.isClient() ? GWT.create(ChunkMap.class) : null;
	
	public interface ChunkMap extends ObjectMapper<Chunk> {
		
	}
	
	public static final ObjectMapper<ChunkId> CHUNKID_MAP = Client.PLATFORM.isClient() ? GWT.create(ChunkIdMap.class) : null;
	
	public interface ChunkIdMap extends ObjectMapper<ChunkId> {
		
	}
	
	public void saveChunk(Chunk chunk) {
		
	}
	
	public void deleteChunk(ChunkId id) {
		
	}
	
	public void updateChunk(Chunk chunk) {
		
	}
	
	
}
