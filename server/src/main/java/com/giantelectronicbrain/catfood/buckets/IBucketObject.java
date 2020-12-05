/**
 * This software is Copyright (C) 2016 Tod G. Harter. All rights reserved.
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

package com.giantelectronicbrain.catfood.buckets;

import java.io.IOException;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;

/**
 * Object held in a bucket.
 * 
 * @author tharter
 *
 */
public interface IBucketObject {
	
	/**
	 * The name of the bucket object.
	 * 
	 * @return the name
	 */
	public IBucketObjectName getName();

	/**
	 * String value of the bucket object's name.
	 * 
	 * @return string name of object
	 */
	public String getNameString();

	/**
	 * Contents of the bucket as a java string. Note that this is mostly a convenience function
	 * which can be used for some simple use cases. It cannot support binary data, for example, and
	 * is not an efficient way to handle large quantities of object data.
	 * 
	 * @return string value of the object's contents
	 * @throws BucketDriverException if the value cannot be returned, etc.
	 * @throws IOException if an error occurs during object I/O
	 */
	public String getContentsAsString() throws BucketDriverException, IOException;

	/**
	 * Get the content of an object as an InputStream. This is the most general way to handle
	 * object contents. It doesn't assume any specific data type and can be used to stream large
	 * amounts of content.
	 * 
	 * @return an InputStream which can be read to get the object's contents
	 * @throws BucketDriverException if a stream cannot be returned, etc.
	 * @throws IOException if there is an error reading data from the object
	 */
	public ReadStream getContentsAsStream() throws BucketDriverException, IOException;

	/**
	 * @param is
	 * @param handler
	 * @throws IOException
	 * @throws BucketDriverException
	 */
	void setContentsAsStream(ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler)
			throws IOException, BucketDriverException;

}
