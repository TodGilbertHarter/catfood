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

import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonDeserializer;
import com.github.nmorel.gwtjackson.client.JsonDeserializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;
import com.github.nmorel.gwtjackson.client.stream.JsonToken;

/**
 * Deserialize a chunk
 * 
 * @author tharter
 *
 */
public class ChunkDeserializer extends JsonDeserializer<Chunk> {

	private static final ChunkDeserializer INSTANCE = new ChunkDeserializer();
	
	public static ChunkDeserializer getInstance() {
		return INSTANCE;
	}
	
	private ChunkDeserializer() {
		
	}
	
	@Override
	protected Chunk doDeserialize(JsonReader reader, JsonDeserializationContext ctx,
			JsonDeserializerParameters params) {
		Chunk chunk = new Chunk();
		reader.beginObject();
		while(!JsonToken.END_OBJECT.equals(reader.peek())) {
			addValue(chunk,reader.nextName(),reader.nextString());
		}
		reader.endObject();
//		reader.close(); //NOTE: commented this out to see if it lets our simple-minded list deserialize work
		return chunk;
	}

	private void addValue(Chunk chunk, String name, String value) {
		if("@rid".equals(name)) {
			chunk.setChunkId(new ChunkId(value.substring(1)));
		} else if("name".equals(name)) {
			chunk.setName(value);
		} else if("content".equals(name)) {
			chunk.setContent(value);
		} else if("lang".equals(name)) {
			chunk.setLang(Language.valueOf(value));
		} else if("lastUpdated".equals(name) ) {
			chunk.setLastUpdated(Long.valueOf(value));
		}
	}
}
