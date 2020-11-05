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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;

/**
 * @author kf203e
 *
 */
public class FsBucketObject implements IBucketObject {
	private final FsBucketObjectName name;
	private final FsBucketDriverImpl driver;

	protected FsBucketObject(FsBucketDriverImpl driver, FsBucketObjectName name) {
		this.driver = driver;
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getName()
	 */
	@Override
	public IBucketObjectName getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getNameString()
	 */
	@Override
	public String getNameString() {
		return getName().getName();
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getContentsAsString()
	 */
	@Override
	public String getContentsAsString() throws BucketDriverException, IOException {
		InputStream is = driver.getInputStream(this);
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString(StandardCharsets.UTF_8.name());
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getContentsAsStream()
	 */
	@Override
	public InputStream getContentsAsStream() throws BucketDriverException, IOException {
		return driver.getInputStream(this);
	}

	@Override
	public void setContentsAsStream(InputStream is) throws IOException, BucketDriverException {
		driver.createBucketObject(this.getName(), is);
	}

}
