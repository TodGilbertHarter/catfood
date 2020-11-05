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
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucket;
import com.giantelectronicbrain.catfood.buckets.IBucketDriver;
import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Filesystem bucket driver implementation. This is useful for testing, etc.
 * 
 * @author kf203e
 *
 */
@Slf4j
@Builder
public class FsBucketDriverImpl implements IBucketDriver {
	
	private final FileSystem fileSystem;
	private final Path basePath;

	private FileAttribute<Set<PosixFilePermission>> makeDefaultAttributes() {
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
		return PosixFilePermissions.asFileAttribute(perms);
	}
	
	private boolean isPosix() {
        Set<String> set=fileSystem.supportedFileAttributeViews();
 
        Iterator<String> iterator=set.iterator();
        while(iterator.hasNext()){
        	if(iterator.next().equalsIgnoreCase("POSIX")) return true;
        }
		return false;
	}
	
	private Path createDirectory(Path dirPath) throws IOException {
		if(isPosix()) {
			return Files.createDirectory(dirPath, makeDefaultAttributes());
		} else {
			return Files.createDirectory(dirPath,new FileAttribute<?>[0]);
		}
	}
	
	private Path createFile(Path filePath) throws IOException {
		if(isPosix()) {
			return Files.createFile(filePath, makeDefaultAttributes());
		} else {
			return Files.createFile(filePath, new FileAttribute<?>[0]);
		}
	}
	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#createBucket(com.boeing.bms.goldstandard.buckets.IBucketName)
	 */
	@Override
	public IBucket createBucket(IBucketName bucketName) throws BucketDriverException {
		try {
			Path path = ((FsBucketName)bucketName).getPath();
			Path fullPath = resolvedPath(path);
			path = createDirectory(fullPath);
		} catch (InvalidPathException | IOException e) {
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
		Path path = ((FsBucketName)bucketName).getPath();
		path = resolvedPath(path);
		if(!Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) // new LinkOption())) // (LinkOption)null))
			bucket = new FsBucket(this, (FsBucketName) bucketName);
		return Optional.ofNullable(bucket);
	}

	private Path resolvedPath(Path path) {
		return basePath.resolve(path);
	}
	
	private Path resolvedPath(FsBucketObjectName boName) {
		Path bop = boName.getPath();
		Path bp = ((FsBucketName) boName.getBucketName()).getPath();
		Path rbp = resolvedPath(bp);
		return rbp.resolve(bop);
	}
	
	InputStream getInputStream(IBucketObject bucketObject) throws IOException {
		Path resolved = resolvedPath(((FsBucketObjectName)bucketObject.getName()));
		return Files.newInputStream(resolved);
	}
	
	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#getBucketObject(com.boeing.bms.goldstandard.buckets.IBucketObjectName)
	 */
	@Override
	public Optional<IBucketObject> getBucketObject(IBucketObjectName objectName) throws BucketDriverException {
		FsBucketObjectName fsbon = (FsBucketObjectName) objectName;
		FsBucketObject bo = null;
		Path path = fsbon.getPath();
		Path bPath = ((FsBucketName) fsbon.getBucketName()).getPath();
		Path fullPath = resolvedPath(bPath);
		fullPath = fullPath.resolve(path); // resolvedPath(fullPath);
		if(!Files.notExists(fullPath))//, (LinkOption[])null
			bo = new FsBucketObject(this,fsbon);
		return Optional.ofNullable(bo);
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#deleteBucket(com.boeing.bms.goldstandard.buckets.IBucketName)
	 */
	@Override
	public boolean deleteBucket(IBucketName bucketName) throws BucketDriverException {
		try {
			Path path = ((FsBucketName)bucketName).getPath();
			Path rpath = resolvedPath(path);
			return FileUtils.deleteQuietly(rpath.toFile());
		} catch (InvalidPathException e) {
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
		private Iterator<Path> pItr; // = Files.list(basePath).iterator();

		public BucketIterator() throws IOException {
			pItr = Files.list(basePath).iterator();
		}
		
		@Override
		public boolean hasNext() {
			return pItr.hasNext();
		}

		@Override
		public IBucket next() {
			try {
				Path p = pItr.next();
				Path bp = p.getFileName();
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
			Path path = resolvedPath((FsBucketObjectName)bucketObjectName);
			// added monkeyshine
			System.out.println("trying to delete "+path);
			log.trace("attempting to delete {}",path);
			return Files.deleteIfExists(path);
		} catch (InvalidPathException | IOException | SecurityException e) {
			e.printStackTrace();
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
			Path rPath = resolvedPath((FsBucketObjectName)bucketObjectName);
			if(!Files.exists(rPath, LinkOption.NOFOLLOW_LINKS)) {
				rPath = createFile(rPath);
				Files.write(rPath, content.getBytes());
				result = true;
			}
		} catch (InvalidPathException | IOException e) {
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
			Path rPath = resolvedPath((FsBucketObjectName)bucketObjectName);
			if(!Files.exists(rPath, LinkOption.NOFOLLOW_LINKS)) {
				Files.copy(content, rPath);
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
		Path path = fileSystem.getPath(name);
		return FsBucketName.builder().path(path).build();
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketDriver#makeBucketObjectName(com.boeing.bms.goldstandard.buckets.IBucketName, java.lang.String)
	 */
	@Override
	public IBucketObjectName makeBucketObjectName(IBucketName bucketName, String name) throws IllegalArgumentException {
		Path path = fileSystem.getPath(name);
		return makeBucketObjectName(bucketName,path);
	}

	private IBucketObjectName makeBucketObjectName(IBucketName bucketName, Path path) throws IllegalArgumentException {
		return FsBucketObjectName.builder().path(path).bucketName(bucketName).build();
	}
	
	protected Iterator<IBucketObject> getBucketObjectIterator(FsBucket fsBucket) throws IOException {
		Path path = ((FsBucketName)fsBucket.getName()).getPath();
		Path fullPath = resolvedPath(path);
		Stream<Path> pStream = Files.list(fullPath);
		Path[] pArray = pStream.toArray(Path[]::new);
		List<Path> pList = Arrays.asList(pArray);
		Iterator<Path> pIter = pList.iterator();
		pStream.close();
		
		return new Iterator<IBucketObject>() {

			@Override
			public boolean hasNext() {
				return pIter.hasNext();
			}

			@Override
			public IBucketObject next() {
				IBucketObject bucketObject = null;
				if(pIter.hasNext()) {
					Path fullPath = pIter.next();
					Path lPath = fullPath.getFileName();
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


}
