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

package com.giantelectronicbrain.catfood.client.chunk;

import com.fasterxml.jackson.annotation.JsonGetter;

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
	@JsonGetter("@rid")
	public String getChunkId() {
		return chunkId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkId == null) ? 0 : chunkId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkId other = (ChunkId) obj;
		if (chunkId == null) {
			if (other.chunkId != null)
				return false;
		} else if (!chunkId.equals(other.chunkId))
			return false;
		return true;
	}

	
}
