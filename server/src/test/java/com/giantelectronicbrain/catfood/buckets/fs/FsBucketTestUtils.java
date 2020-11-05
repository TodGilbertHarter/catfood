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

package com.giantelectronicbrain.catfood.buckets.fs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucketDriver;

/**
 * Put all the utility classes for testing FS buckets into one place.
 * 
 * @author tharter
 *
 */
public class FsBucketTestUtils {

	public static IBucketDriver createUUT() throws BucketDriverException {
		FileSystem fileSystem = FileSystems.getDefault();
		Path path = fileSystem.getPath("./build/buckettests");

		return  FsBucketDriverImpl.builder()
				.fileSystem(fileSystem)
				.basePath(path)
				.build();
		
	}

	public static void cleanUpBuckets() throws IOException {
		FileSystem fileSystem = FileSystems.getDefault();
		Path path = fileSystem.getPath("./build/buckettests/testbucket");
		FileUtils.deleteDirectory(path.toFile()); // .deleteRecursive(path.toString(), true);
	}
	
	public static void setUpBuckets() throws IOException {
		FileSystem fileSystem = FileSystems.getDefault();
		Path buildPath = fileSystem.getPath("./build");
		if(!Files.exists(buildPath, LinkOption.NOFOLLOW_LINKS)) {
			createDirectory(buildPath);
		}
		Path path2 = fileSystem.getPath("./build/buckettests/testbucket2");
		FileUtils.deleteDirectory(path2.toFile()); // deleteRecursive(path2.toString(), true);
		Path path = fileSystem.getPath("./build/buckettests");
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			createDirectory(path);
		}
		path = path.resolve("testbucket");
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			createDirectory(path);
		}
		path = path.resolve("testobject");
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			createFile(path);
		}
	}

	private static FileAttribute<Set<PosixFilePermission>> makeDefaultAttributes() {
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
		return PosixFilePermissions.asFileAttribute(perms);
	}
	
	private static boolean isPosix() {
        FileSystem fs=FileSystems.getDefault();
        Set<String> set=fs.supportedFileAttributeViews();
 
        Iterator<String> iterator=set.iterator();
        while(iterator.hasNext()){
        	if(iterator.next().equalsIgnoreCase("POSIX")) return true;
        }
		return false;
	}
	
	private static Path createDirectory(Path dirPath) throws IOException {
		if(isPosix()) {
			return Files.createDirectory(dirPath, makeDefaultAttributes());
		} else {
			return Files.createDirectory(dirPath,new FileAttribute<?>[0]);
		}
	}
	
	private static Path createFile(Path filePath) throws IOException {
		if(isPosix()) {
			return Files.createFile(filePath, makeDefaultAttributes());
		} else {
			return Files.createFile(filePath, new FileAttribute<?>[0]);
		}
	}

}
