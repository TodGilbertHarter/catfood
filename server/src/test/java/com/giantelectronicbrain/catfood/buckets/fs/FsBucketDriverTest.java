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

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.BucketDriverTest;
import com.giantelectronicbrain.catfood.buckets.IBucketDriver;

/**
 * Runs the BucketDriver tests against the FsBucketDriverImpl.
 * 
 * @author tharter
 *
 */
public class FsBucketDriverTest extends BucketDriverTest {

	@Override
	protected IBucketDriver createUUT() throws BucketDriverException {
		return FsBucketTestUtils.createUUT();
//		FileSystem fileSystem = FileSystems.getDefault();
//		Path path = fileSystem.getPath("./build");

//		return FsBucketDriverImpl.builder()
//				.fileSystem(fileSystem)
//				.basePath(path)
//				.build();
	}

	@Override
	protected void cleanUpBuckets() throws IOException {
		FsBucketTestUtils.cleanUpBuckets();
//		FileSystem fileSystem = FileSystems.getDefault();
//		Path path = fileSystem.getPath("./build/testbucket");
//		FileUtils.deleteRecursive(path.toString(), true);
	}
	
	@Override
	protected void setUpBuckets() throws IOException {
		FsBucketTestUtils.setUpBuckets();
/*		FileSystem fileSystem = FileSystems.getDefault();
		Path path2 = fileSystem.getPath("./build/testbucket2");
		FileUtils.deleteRecursive(path2.toString(), true);
		Path path = fileSystem.getPath("./build");
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			Files.createDirectory(path);
		}
		path = path.resolve("testbucket");
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			Files.createDirectory(path);
		}
		path = path.resolve("testobject");
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			Files.createFile(path);
		} */
		
		// TODO Auto-generated method stub
		
	}

	

}
