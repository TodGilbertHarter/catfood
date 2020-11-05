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

import java.util.List;
import java.util.function.BiConsumer;

import com.giantelectronicbrain.catfood.client.Client;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.UriUtils;
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

	private final String GET_BYID_CHUNK_PORT = "/data/chunk/byid";
	private final String DELETE_CHUNK_PORT = "/data/chunk/byid";
	private final String GET_BYNAME_CHUNK_PORT = "/data/chunk/byname";
	private final String POST_CHUNK_PORT = "/data/chunk";
	private final String PUT_CHUNK_PORT = "/data/chunk";
	private final String FIND_CHUNK_BYNAME = "/data/chunk/like/";

	public void findChunk(String name, BiConsumer<Integer,List<Chunk>> handler) {
		String url = FIND_CHUNK_BYNAME + "/" + UriUtils.encode(name);
		getObject(CHUNK_LIST_MAP, url, handler);
	}
	
	
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
		String url = GET_BYNAME_CHUNK_PORT + "/" + UriUtils.encode(name);
		getObject(CHUNK_MAP,url,handler);
	}
	
	public void saveChunk(Chunk chunk, BiConsumer<Integer,ChunkId> handler) {
		String url = POST_CHUNK_PORT;
		postObject(chunk,CHUNK_MAP,CHUNKID_MAP,url,handler);
	}
	
	public void deleteChunk(ChunkId id) {
		String url = DELETE_CHUNK_PORT;
		deleteObject(id,url);
	}
	
	public void updateChunk(Chunk chunk, BiConsumer<Integer,ChunkId> handler) {
		String url = PUT_CHUNK_PORT;
		putObject(chunk,CHUNK_MAP,CHUNKID_MAP,url,handler);
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

	private <K,O> void postObject(O object, ObjectMapper<O> inMapper, ObjectMapper<K> outMapper, String query, BiConsumer<Integer,K> handler) {
		_sendObject("POST",object,inMapper,outMapper,query,handler);
	}
	
	private <K,O> void putObject(O object, ObjectMapper<O> inMapper, ObjectMapper<K> outMapper, String query, BiConsumer<Integer,K> handler) {
		_sendObject("PUT",object,inMapper,null,query,handler);
	}
	
	private void deleteObject(ChunkId id, String query) {
		String url = query + "/" + id.getChunkId();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("DELETE",query);
		xhr.send();
	}
	
	private <K,O> void _sendObject(String method, O object, ObjectMapper<O> inMapper, ObjectMapper<K> outMapper, String query, BiConsumer<Integer,K> handler) {
		String requestData = in(object,inMapper);
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(a -> {
			if(handler == null || xhr.getReadyState() != 4) {
				return;
			}
			K result = null;
			if(xhr.getStatus() == 200) {
				result = out(xhr.getResponseText(),outMapper);
			}
			handler.accept(xhr.getStatus(), result);
		});
		
		xhr.open(method, query);
		xhr.send(requestData);
	}

	private static <O> String in(O object, ObjectMapper<O> inMapper) {
		if(object == null || inMapper == null) {
			return null;
		} else {
			return inMapper.write(object);
		}
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
	
	public static final ObjectMapper<List<Chunk>> CHUNK_LIST_MAP = Client.PLATFORM.isClient() ? GWT.create(ChunkListMap.class) : null;
	
	public interface ChunkListMap extends ObjectMapper<List<Chunk>> {
		
	}

}
