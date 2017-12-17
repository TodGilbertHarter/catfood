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

package com.giantelectronicbrain.catfood.model;

/**
 * DTO representing a chunk of CatFood content.
 * 
 * @author tharter
 *
 */
public class Chunk {

	private ChunkId chunkId;
	private String content;
	
	/**
	 * Instantiate a content chunk.
	 */
	public Chunk() {
	}

	/**
	 * Instantiate a chunk with the given content.
	 * 
	 * @param content initial content for this chunk
	 * @param the ChunkId of this chunk, or null if it hasn't got one.
	 */
	public Chunk(String content, ChunkId chunkId) {
		setContent(content);
		setChunkId(chunkId);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ChunkId getChunkId() {
		return chunkId;
	}

	public void setChunkId(ChunkId chunkId) {
		this.chunkId = chunkId;
	}
	
	
}
