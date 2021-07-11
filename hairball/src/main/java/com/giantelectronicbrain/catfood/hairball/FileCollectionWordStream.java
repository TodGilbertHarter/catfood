/**
 * This software is Copyright (C) 2021 Tod G. Harter. All rights reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

/**
 * A Word Stream which handles a whole group of files as a single input, reading
 * one file after the other in the order the arguments are presented.
 * 
 * @author tharter
 *
 */
public class FileCollectionWordStream implements IWordStream {

	private final FileSystem fileSystem;
	private final String bucketName;
	private final List<String> objectNames;
	
	private IWordStream wordStream = null;
	
	/**
	 * Create a word stream which will supply words from a series of objects in a bucket.
	 * 
	 * @param vertx
	 * @param bucketName
	 * @param objectNames
	 */
	public FileCollectionWordStream(Vertx vertx, String bucketName, String[] objectNames) {
		this.fileSystem = vertx.fileSystem();
		this.bucketName = bucketName;
		this.objectNames = new ArrayList<String>();
		Collections.addAll(this.objectNames,objectNames);
	}

	/**
	 * Return a wordstream, either the current one, or the next one if the current one doesn't
	 * exist or is exhausted.
	 * 
	 * @return the next wordstream, or null if no more exist
	 * @throws IOException 
	 */
	private IWordStream getWordStream() throws IOException {
		if(wordStream == null || wordStream.hasMoreTokens() != true) {
			if(objectNames.size() > 0) {
				String objectName = objectNames.remove(0);
//System.out.println("GOT AN OBJECT OF NAME "+objectName);
				wordStream = new BucketWordStream(fileSystem,objectName,bucketName);
			} else {
				return null;
			}
		}
		return wordStream;
	}
	
	@Override
	public Word getNextWord() throws IOException {
//System.out.println("GETTING NEXT WORD");
		IWordStream ws = getWordStream();
		return ws == null ? null : ws.getNextWord();
	}

	@Override
	public String getToMatching(String match) throws IOException {
//System.out.println("GET TO MATCHING");
		IWordStream ws = getWordStream();
//System.out.println("GOT BACK WS OF "+ws);
		return ws == null ? null : ws.getToMatching(match);
	}

	@Override
	public boolean hasMoreTokens() throws IOException {
//System.out.println("HAS MORE TOKENS in FileCollectionWordStream");
		IWordStream ws = getWordStream();
		return ws == null ? false : ws.hasMoreTokens();
	}

}
