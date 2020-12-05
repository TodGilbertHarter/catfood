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
package com.giantelectronicbrain.catfood.buckets.fs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucket;
import com.giantelectronicbrain.catfood.buckets.IBucketDriver;
import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;
import com.giantelectronicbrain.catfood.exceptions.CatfoodApplicationException;
import com.giantelectronicbrain.catfood.exceptions.ExceptionIds;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.FileSystemException;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Filesystem bucket driver implementation. This is useful for testing, etc.
 * 
 * @author tharter
 *
 */
@Slf4j
@Builder
public class FsBucketDriverImpl implements IBucketDriver {
	
	private final FileSystem fileSystem;
	private final String basePath;

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#createBucket(com.boeing.bms.goldstandard.buckets.IBucketName)
	 */
	@Override
	public IBucket createBucket(IBucketName bucketName) throws BucketDriverException {
		try {
			String path = bucketName.getNameString();
			path = resolvedPath(path);
			fileSystem.mkdirBlocking(path);
		} catch (FileSystemException e) {
			log.warn("Exception while creating a bucket",e);
			throw new BucketDriverException("Cannot create a bucket",e);
		}
		return new FsBucket(this, (FsBucketName)bucketName);
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#getBucket(com.boeing.bms.goldstandard.buckets.IBucketName)
	 */
	@Override
	public Optional<IBucket> getBucket(IBucketName bucketName) throws BucketDriverException {
		FsBucket bucket = null;
		String path = bucketName.getNameString();
		path = resolvedPath(path);
		if(fileSystem.existsBlocking(path))
			bucket = new FsBucket(this, (FsBucketName) bucketName);
		return Optional.ofNullable(bucket);
	}

	private String resolvedPath(String path) {
		return basePath + "/" + path;
	}
	
	private String resolvedPath(FsBucketObjectName boName) {
		String bop = boName.getName();
		String bp = boName.getBucketName().getNameString();
		return resolvedPath(bp) + "/" + bop;
	}
	
/*	private InputStream getInputStream(IBucketObject bucketObject) throws IOException {
		String resolved = resolvedPath(bucketObject.getNameString());
		Buffer buffer = fileSystem.readFileBlocking(resolved);
		buffer.
	} */
	
	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#getBucketObject(com.boeing.bms.goldstandard.buckets.IBucketObjectName)
	 */
	@Override
	public Optional<IBucketObject> getBucketObject(IBucketObjectName objectName) throws BucketDriverException {
		FsBucketObjectName fsbon = (FsBucketObjectName) objectName;
		String fullPath = resolvedPath(fsbon);
		IBucketObject bo = null;
		if(fileSystem.existsBlocking(fullPath))
			bo = new FsBucketObject(this,fsbon);
		return Optional.ofNullable(bo);
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#deleteBucket(com.boeing.bms.goldstandard.buckets.IBucketName)
	 */
	@Override
	public boolean deleteBucket(IBucketName bucketName) throws BucketDriverException {
		try {
			String path = bucketName.getNameString();
			String rpath = resolvedPath(path);
			fileSystem.deleteBlocking(rpath);
			return true; //TODO: it would be nice to be able to actually return a meaningful result here
		} catch (FileSystemException e) {
			log.warn("Exception while deleting a bucket",e);
			throw new BucketDriverException("Cannot delete a bucket",e);
		}
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#getBucketIterator()
	 */
	@Override
	public Iterator<IBucket> getBucketIterator() throws BucketDriverException {
		try {
			return new BucketIterator();
		} catch (IOException e) {
			throw new BucketDriverException("Can't create bucket iterator",e);
		}
	}

	private class BucketIterator implements Iterator<IBucket> {
		private Iterator<String> pItr; // = Files.list(basePath).iterator();

		public BucketIterator() throws IOException {
			pItr = fileSystem.readDirBlocking(basePath).iterator();
		}
		
		@Override
		public boolean hasNext() {
			return pItr.hasNext();
		}

		@Override
		public IBucket next() {
			try {
				String p = pItr.next();
				String bp = getFileName(p);
				IBucketName bn = new FsBucketName(bp);
				Optional<IBucket> bo = getBucket(bn);
				return bo.get();
			} catch (Exception e) {
				NoSuchElementException f = new NoSuchElementException("Iterator cannot return element");
				f.initCause(e);
				throw f;
			}
		}

	}
	
	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#deleteBucketObject(com.boeing.bms.goldstandard.buckets.IBucketObjectName)
	 */
	@Override
	public boolean deleteBucketObject(IBucketObjectName bucketObjectName) throws BucketDriverException {
		try {
			String path = resolvedPath((FsBucketObjectName)bucketObjectName);
			log.trace("attempting to delete {}",path);
			fileSystem.deleteBlocking(path);
			return true;
		} catch (InvalidPathException | SecurityException e) {
			log.warn("Exception while deleting a bucketObject",e);
			throw new BucketDriverException("Cannot delete a bucketObject",e);
		}
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#createBucketObject(com.boeing.bms.goldstandard.buckets.IBucketObjectName, java.lang.String)
	 */
	@Override
	public boolean createBucketObject(IBucketObjectName bucketObjectName, String content) throws BucketDriverException {
		boolean result = false;
		try {
//			Path path = ((FsBucketObjectName)bucketObjectName).getPath();
			String rPath = resolvedPath((FsBucketObjectName)bucketObjectName);
			if(!fileSystem.existsBlocking(rPath)) {
				fileSystem.createFileBlocking(rPath);
				fileSystem.writeFileBlocking(rPath, Buffer.buffer(content.getBytes()));
				result = true;
			}
		} catch (InvalidPathException e) {
			log.warn("Exception while creating a bucketObject",e);
			throw new BucketDriverException("Cannot create a bucketObject",e);
		}
		return result;
	}

	@Override
	public boolean createBucketObject(IBucketObjectName bucketObjectName, InputStream content)
			throws BucketDriverException {
		boolean result = false;
		try {
//			Path path = ((FsBucketObjectName)bucketObjectName).getPath();
			String rPath = resolvedPath((FsBucketObjectName)bucketObjectName);
			if(!fileSystem.existsBlocking(rPath)) {
				fileSystem.writeFileBlocking(rPath,Buffer.buffer(content.readAllBytes()));
				result = true;
			}
		} catch (InvalidPathException | IOException e) {
			log.warn("Exception while creating a bucketObject",e);
			throw new BucketDriverException("Cannot create a bucketObject",e);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#makeBucketName(java.lang.String)
	 */
	@Override
	public IBucketName makeBucketName(String name) throws IllegalArgumentException {
		return FsBucketName.builder().nameString(name).build();
	}

	@Override
	public IBucketObjectName makeBucketObjectName(IBucketName bucketName, String path) throws IllegalArgumentException {
		return FsBucketObjectName.builder().name(path).bucketName(bucketName).build();
	}

	private String getFileName(String path) {
		int index = path.lastIndexOf('/') + 1;
		return index == 0 ? path : path.substring(index);
	}
	
	protected Iterator<IBucketObject> getBucketObjectIterator(FsBucket fsBucket) throws IOException {
		String path = fsBucket.getName().getNameString();
		String fullPath = resolvedPath(path);
		List<String> pList = fileSystem.readDirBlocking(fullPath);
		Iterator<String> pIter = pList.iterator();
		
		return new Iterator<IBucketObject>() {

			@Override
			public boolean hasNext() {
				return pIter.hasNext();
			}

			@Override
			public IBucketObject next() {
				IBucketObject bucketObject = null;
				if(pIter.hasNext()) {
					String fullPath = pIter.next();
					String lPath = getFileName(fullPath);
					IBucketName bName = fsBucket.getName();
					IBucketObjectName bucketObjectName = makeBucketObjectName(bName, lPath);
					try {
						bucketObject = getBucketObject(bucketObjectName).get();
					} catch (BucketDriverException e) {
						//TODO: well, uh, this is what you get when you use this sort of interface!
					}
				}
				return bucketObject;
			}
			
		};
	}

	/**
	 * Given a bucket object and a desired character set, read the contents of the object into
	 * a string encoded in the given character set, and return it.
	 * 
	 * @param bucketObject the bucket object to read
	 * @param charset the character set the data is encoded with
	 * @return contents of the bucket object as a string
	 */
	@Override
	public String getBucketObjectContentsAsString(IBucketObject bucketObject, Charset charset) {
		String path = resolvedPath((FsBucketObjectName)bucketObject.getName());
		Buffer buffer = fileSystem.readFileBlocking(path);
		byte[] bytes = buffer.getBytes();
		return new String(bytes,charset);
	}

	@Override
	public IBucketDriver createBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBucketDriver getBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBucketDriver getBucketObject(IBucketObjectName objectName, Handler<AsyncResult<IBucketObject>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBucketDriver deleteBucket(IBucketName bucketName, Handler<AsyncResult<Void>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBucketDriver deleteBucketObject(IBucketObjectName bucketObjectName, Handler<AsyncResult<Void>> hanlder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, String content,
			Handler<AsyncResult<Void>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, InputStream content,
			Handler<AsyncResult<Void>> handler) {
		Future<Void> ar = Future.future(promise -> {
			try {
				String rPath = resolvedPath((FsBucketObjectName)bucketObjectName);
				fileSystem.open(rPath, new OpenOptions().setCreate(true).setWrite(true), result -> {
					if(result.succeeded()) {
						AsyncFile file = result.result();
						byte[] bytes = new byte[1024];
						int nRead;
						do {
							try {
								nRead = content.read(bytes);
								if(nRead != -1) {
									Buffer buffer = Buffer.buffer(bytes);
									file.write(buffer,nRead);
								}
							} catch (IOException e) {
								//TODO: log something
								promise.fail(e);
								return;
							}
						} while(nRead != -1);
						promise.complete();
					} else {
						//TODO: log something
						promise.fail(result.cause());
					}
				});
			} catch(Exception e) {
				//TODO: probably should log something here
				promise.fail(e);
			}
		});
	return this;
	}
		
	@Override
	public IBucketDriver makeBucketName(String name, Handler<AsyncResult<IBucketName>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBucketDriver makeBucketObjectName(IBucketName bucketName, String name,
			Handler<AsyncResult<IBucketObjectName>> handler) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReadStream<Buffer> getReadStream(FsBucketObject fsBucketObject) {
		IBucketObjectName bucketObjectName = fsBucketObject.getName();
		String rPath = resolvedPath((FsBucketObjectName)bucketObjectName);
		Future<AsyncFile> ff = fileSystem.open(rPath, new OpenOptions());
		return ff.result();
	}

	//TODO: add Handler<AsyncResult<Void>> to this...
	@Override
	public void createBucketObject(IBucketObjectName name, ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler) {
		String path = resolvedPath((FsBucketObjectName)name);
		fileSystem.open(path, new OpenOptions(), result -> {
			if(result.succeeded()) {
				AsyncFile file = result.result();
				Future<Void> pfut = is.pipeTo(file);
				handler.handle(pfut);
			} else {
				Throwable t = makeException(result.cause());
				handler.handle(Future.failedFuture(t));
			}
		});
	}

	@Override
	public void createBucketObject(IBucketObjectName name, Handler<AsyncResult<WriteStream<Buffer>>> handler) {
		String path = resolvedPath((FsBucketObjectName)name);
		fileSystem.open(path, new OpenOptions(), result -> {
			if(result.succeeded()) {
				AsyncFile file = result.result();
				AsyncResult<WriteStream<Buffer>> ffut = Future.succeededFuture(file);
				handler.handle(ffut);
			} else {
				Throwable t = makeException(result.cause());
				handler.handle(Future.failedFuture(t));
			}
		});
	}

	private CatfoodApplicationException makeException(Throwable cause) {
		return new CatfoodApplicationException(ExceptionIds.SERVER_ERROR,"","");
	}

	@Override
	public void setContentsAsStream(ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler)
			throws IOException, BucketDriverException {
		// TODO Auto-generated method stub
		
	}

}
