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
 * Representation of the id of a chunk.
 * 
 * @author tharter
 *
 */
public class ChunkId {

	private final String chunkId;
	
	/**
	 * Instantiate a chunk id.
	 */
	public ChunkId(String chunkId) {
		this.chunkId = chunkId;
	}
	
	/**
	 * Get the String representation of a ChunkId
	 * 
	 * @return chunkId string
	 */
	public String getChunkId() {
		return chunkId;
	}

	
}
