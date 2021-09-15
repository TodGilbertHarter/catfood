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
package com.giantelectronicbrain.catfood.assets;

import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;
import com.giantelectronicbrain.catfood.buckets.fs.FsBucketDriverImpl;
import com.giantelectronicbrain.catfood.exceptions.CatfoodApplicationException;
import com.giantelectronicbrain.catfood.exceptions.ExceptionIds;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.RoutingContext;

/**
 * File System asset store implementation.
 * 
 * @author tharter
 *
 */
public class FSAssetStore implements IAssetStore {
	private final FsBucketDriverImpl bucketDriver;

	public FSAssetStore(String basePath, FileSystem fileSystem) {
//		bucketDriver = FsBucketDriverImpl.builder().basePath(basePath).fileSystem(fileSystem).build();
		bucketDriver = FsBucketDriverImpl.builder().fileSystem(fileSystem).build();
	}
	
	private IBucketName getBucketName() {
		return bucketDriver.makeBucketName("assets");
	}
	
	@Override
	public void createAsset(RoutingContext context) {
		String name = context.request().getParam("assetname");
		IBucketName bucketName = getBucketName();
		IBucketObjectName bObjName = bucketDriver.makeBucketObjectName(bucketName,name);
		bucketDriver.createBucketObject(bObjName, result -> {
			if(result.succeeded()) {
				WriteStream<Buffer> ws = result.result();
				context.request().pipeTo(ws, pResult -> {
					if(pResult.succeeded()) {
						sendResult(context,null);
					} else {
						Throwable t = new CatfoodApplicationException(ExceptionIds.SERVER_ERROR,"","");
						t.initCause(pResult.cause());
						context.fail(t);
					}
				});
			} else {
				Throwable t = new CatfoodApplicationException(ExceptionIds.SERVER_ERROR,"","");
				t.initCause(result.cause());
				context.fail(t);
			}
		});
	}
	
	private void sendResult(final RoutingContext routingContext, final Object result) {
		JsonObject jo = JsonObject.mapFrom(result);
		sendResult(routingContext,jo.encode());
	}
	
	private void sendResult(final RoutingContext routingContext, final String json) {
		routingContext.response()
			.putHeader("content-type","application/json; charset=utf-8")
			.end(json == null ? "" : json);
		
	}

}
