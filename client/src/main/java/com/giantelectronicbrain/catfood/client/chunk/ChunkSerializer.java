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

import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializer;
import com.github.nmorel.gwtjackson.client.JsonSerializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonWriter;

/**
 * @author tharter
 *
 */
public class ChunkSerializer extends JsonSerializer<Chunk> {

	private static final ChunkSerializer INSTANCE = new ChunkSerializer();
	
	public static ChunkSerializer getInstance() {
		return INSTANCE;
	}
	
	private ChunkSerializer() {
		
	}
	
	@Override
	protected void doSerialize(JsonWriter writer, Chunk chunk, JsonSerializationContext ctx, JsonSerializerParameters params) {
		writer.beginObject();
		writer.name("content").value(chunk.getContent());
		writer.name("name").value(chunk.getName());
		writer.name("lang").value(chunk.getLang().name());
		writer.name("@rid").value(chunk.getChunkId().getChunkId());
		writer.endObject();
	}

}
