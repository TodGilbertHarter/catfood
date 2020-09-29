/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
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

package com.giantelectronicbrain.catfood.store;

import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.ChunkId;

/**
 * Interface for CatFood Database modules to implement.
 * 
 * @author tharter
 *
 */
public interface ICatFoodDBStore {

	public abstract void start();
	
	public abstract void stop();
	
	/**
	 * Save a chunk with a new id. If the chunk has an existing id, it
	 * will be ignored, but no changes will be made to the id in the
	 * input chunk.
	 * 
	 * @param chunk the chunk to save.
	 * @return id allocated to the chunk
	 */
	public abstract ChunkId postContent(Chunk chunk);
	
	/**
	 * Get a chunk of CatFood content with the given id.
	 * 
	 * @param id the identifier for this chunk.
	 * @return the content Chunk
	 */
	public abstract Chunk getContent(ChunkId id);
	
	/**
	 * Get a chunk of CatFood content with the given id and
	 * return it as a JSON string.
	 * 
	 * @param id the identifier for this chunk.
	 * @return String the content chunk
	 */
	public abstract String getJsonContent(ChunkId id);

	/**
	 * Get a CatFood Chunk by topic and return it as JSON.
	 * 
	 * @param name topic name.
	 * @return String the topic.
	 */
	public abstract String getJsonChunk(String name);

	/**
	 * @param chunk
	 * @throws StorageException
	 */
	public abstract void putContent(Chunk chunk) throws StorageException;
}
