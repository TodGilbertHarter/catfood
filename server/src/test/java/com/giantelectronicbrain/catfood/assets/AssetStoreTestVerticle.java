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


import com.giantelectronicbrain.catfood.CatFoodAssetService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * @author tharter
 *
 */
public class AssetStoreTestVerticle extends AbstractVerticle {
	
	HttpServer server;
	CatFoodAssetService assetService;
	

	@Override
	public void start(Promise<Void> startFuture) throws Exception {
	    System.out.println("VERTICLE 1");
		IAssetStore assetStore = new FSAssetStore("./build/buckettests",vertx.fileSystem());
	    System.out.println("VERTICLE 2");
		assetService = new CatFoodAssetService(assetStore);
	    System.out.println("VERTICLE 3");
		
		server = vertx.createHttpServer();
	    System.out.println("VERTICLE 4");
		Router router = Router.router(vertx);
	    System.out.println("VERTICLE 5");
		router.post("/assets/:assetname").handler(routingContext -> {
			assetService.postAsset(routingContext);
		});
		server.requestHandler(req -> router.handle(req));
	    System.out.println("VERTICLE 6");
		Future<HttpServer> result = server.listen();
		if(result.failed()) {
			Throwable e = result.cause();
			e.printStackTrace();
		}
	    System.out.println("VERTICLE 7");
	}
}
