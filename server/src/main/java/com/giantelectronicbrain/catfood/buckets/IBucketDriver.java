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
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Optional;

import com.giantelectronicbrain.catfood.buckets.fs.FsBucketObject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;

/**
 * Interface abstraction for accessing bucket stores. This is intended to work with things like
 * Amazon AWS S3, Swift, and various flavors of these and other similar APIs. By implementing this
 * interface you should be able to use these services interchangeably, implement your application on
 * top of different service provider libraries, etc.
 * 
 * @author tharter
 *
 */
public interface IBucketDriver {

	/**
	 * Create a new bucket with the given name.
	 * 
	 * @param bucketName IBucketName the name of the new bucket.
	 * @return IBucket the new bucket.
	 * 
	 * @throws BucketDriverException if there is an error.
	 */
	public IBucket createBucket(IBucketName bucketName) throws BucketDriverException;
	
	/**
	 * Create a new bucket with the given name. The handler will be invoked when the
	 * bucket has been created.
	 * 
	 * @param bucketName IBucketName the name of the new bucket.
	 * @param handler invoked when the bucket is created.
	 */
	public IBucketDriver createBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler);
	
	/**
	 * Get a bucket with the given name. If no such bucket exists then return null. Note that
	 * some drivers don't have an efficient way to check existence!
	 * 
	 * @param bucketName IBucketName the name of the new bucket.
	 * @return Optional<IBucket> the bucket, or void if it doesn't exist.
	 * 
	 * @throws BucketDriverException if there is an error.
	 */
	public Optional<IBucket> getBucket(IBucketName bucketName) throws BucketDriverException;

	/**
	 * Get a bucket with the given name. Call the handler when it is available.
	 * 
	 * @param bucketName IBucketName the name of the new bucket.
	 * @param handler called when the bucket has been fetched
	 */
	public IBucketDriver getBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler);
	
	/**
	 * Get a specific named object. Note that this returns the entire object, so it is not the
	 * best choice for large objects.
	 * 
	 * @param objectName name of the object
	 * @return IBucketObject containing the object
	 * @throws BucketDriverException
	 */
	public Optional<IBucketObject> getBucketObject(IBucketObjectName objectName) throws BucketDriverException;

	/**
	 * Get a specific named object. Note that this returns the entire object, so it is not the
	 * best choice for large objects.
	 * 
	 * @param objectName name of the object
	 * @param handler handles the returned object
	 */
	public IBucketDriver getBucketObject(IBucketObjectName objectName, Handler<AsyncResult<IBucketObject>> handler);
	
	/**
	 * Delete a bucket.
	 * 
	 * @param bucketName bucket to delete
	 * @return true on success, false if no bucket was deleted (IE no such bucket exists)
	 * @throws BucketDriverException if there is an error during bucket deletion
	 */
	public boolean deleteBucket(IBucketName bucketName) throws BucketDriverException;

	/**
	 * Delete a bucket.
	 * 
	 * @param bucketName bucket to delete
	 * @param handler handle deletion
	 */
	public IBucketDriver deleteBucket(IBucketName bucketName, Handler<AsyncResult<Void>> handler);
	
	/**
	 * Get an iterator on all buckets In the scope of the currently initialized driver.
	 * 
	 * @return a bucket iterator
	 * @throws BucketDriverException 
	 */
	public Iterator<IBucket> getBucketIterator() throws BucketDriverException;

	/**
	 * Delete an object from a bucket.
	 * @param bucketObjectName the name of the object
	 * @return true if object deleted false if not
	 * @throws BucketDriverException if there is an error during object deletion
	 */
	public boolean deleteBucketObject(IBucketObjectName bucketObjectName) throws BucketDriverException;

	/**
	 * Delete an object from a bucket.
	 * 
	 * @param bucketObjectName the name of the object
	 * @param handler handle the results
	 */
	public IBucketDriver deleteBucketObject(IBucketObjectName bucketObjectName, Handler<AsyncResult<Void>> hanlder);
	
	/**
	 * Create a bucket object with the given content.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content string to set the object's value to
	 * @return true if object was created, false otherwise
	 * @throws BucketDriverException if there was an error during object creation
	 */
	public boolean createBucketObject(IBucketObjectName bucketObjectName, String content) throws BucketDriverException;
	
	/**
	 * Create a bucket object with the given content.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content string to set the object's value to
	 * @param handler handle the results
	 */
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, String content, Handler<AsyncResult<Void>> handler);

	/**
	 * Create a bucket object with the given content.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content InputStream holding new bucket contents
	 * @return true if object was created, false otherwise
	 * @throws BucketDriverException if there was an error during object creation
	 */
	public boolean createBucketObject(IBucketObjectName bucketObjectName, InputStream content) throws BucketDriverException;

	/**
	 * Create a bucket object with the given content.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content InputStream holding new bucket contents
	 * @param handler handle the results
	 */
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, InputStream content, Handler<AsyncResult<Void>> handler);

	/**
	 * Create a bucket name object with the given string as its value. Note that implementations may put restrictions on 
	 * the values allowed for these names.
	 * 
	 * @param name the string value of the bucket's name.
	 * @return an IBucketName
	 * @throws IllegalArgumentException if the string value is not legal in the implementation
	 */
	public IBucketName makeBucketName(String name) throws IllegalArgumentException;
	
	/**
	 * Create a bucket name object with the given string as its value. Note that implementations may put restrictions on 
	 * the values allowed for these names.
	 * 
	 * @param name the string value of the bucket's name.
	 * @param handler handle the results
	 */
	public IBucketDriver makeBucketName(String name, Handler<AsyncResult<IBucketName>> handler);
	
	/**
	 * Create a bucket object name with the given string value and in the scope of the given IBucketName. Both the context
	 * and the implementation may restrict which values are allowed for an object.
	 * 
	 * @param bucketName The bucket name under which this object name will exist
	 * @param name the string value of the object's name
	 * @return an IBucketObjectName with the given value
	 * @throws IllegalArgumentException if this name violates the rules of the implmentation naming scheme
	 */
	public IBucketObjectName makeBucketObjectName(IBucketName bucketName, String name) throws IllegalArgumentException;

	/**
	 * Create a bucket object name with the given string value and in the scope of the given IBucketName. Both the context
	 * and the implementation may restrict which values are allowed for an object name.
	 * 
	 * @param bucketName The bucket name under which this object name will exist
	 * @param name the string value of the object's name
	 * @param handler handle the results
	 */
	public IBucketDriver makeBucketObjectName(IBucketName bucketName, String name, Handler<AsyncResult<IBucketObjectName>> handler) throws IllegalArgumentException;

	/**
	 * Get the contents of a bucket object as a string. Note that this is only going to work for buckets which
	 * contain text, and is not an efficient way to process large objects.
	 * 
	 * @param bucketObject the object who's contents to get.
	 * @param charset the character set which encodes the texts code points
	 * @return contents of the bucket object as a string.
	 */
	public abstract String getBucketObjectContentsAsString(IBucketObject bucketObject, Charset charset);

	/**
	 * @param fsBucketObject
	 * @return
	 */
	public ReadStream<Buffer> getReadStream(FsBucketObject IBucketObject);

	/**
	 * @param name
	 * @param is
	 */
	public void createBucketObject(IBucketObjectName name, ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler);

	/**
	 * @param name
	 * @param handler
	 */
	public void createBucketObject(IBucketObjectName name, Handler<AsyncResult<WriteStream<Buffer>>> handler);

	public void setContentsAsStream(ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler) throws IOException, BucketDriverException;

}
