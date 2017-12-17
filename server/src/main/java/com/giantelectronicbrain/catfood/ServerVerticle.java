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

package com.giantelectronicbrain.catfood;

import com.giantelectronicbrain.catfood.initialization.IInitializer;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;

/**
 * Verticle which acts as a CatFood HTTP server.
 * 
 * @author tharter
 *
 */
public class ServerVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerVerticle.class);
	private IInitializer initializer = InitializerFactory.getInitializer();
	
	private HttpServer server;
	private CatFoodDBService dBService = (CatFoodDBService) initializer.get(InitializerFactory.CATFOOD_DB_SERVICE);
	private String orientdbHome = (String) initializer.get(InitializerFactory.ORIENTDB_HOME);
	private Integer port = (Integer) initializer.get(InitializerFactory.PORT);
	private TemplateHandler templateHandler = (TemplateHandler) initializer.get(InitializerFactory.JSX_TEMPLATEHANDLER);
	private StaticHandler otherHandler = (StaticHandler) initializer.get(InitializerFactory.STATIC_HANDLER);
	private StaticHandler libsHandler = (StaticHandler) initializer.get(InitializerFactory.LIBS_HANDLER);
	private TemplateHandler jsxLibsHandler = (TemplateHandler) initializer.get(InitializerFactory.JSX_LIBSHANDLER);
	private StaticHandler componentsHandler = (StaticHandler) initializer.get(InitializerFactory.COMPONENTS_HANDLER);
	
	/**
	 * Instantiate the CatFood HTTP Server Verticle.
	 */
	public ServerVerticle() {
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		vertx.executeBlocking( future -> {
			try {
			    System.setProperty("ORIENTDB_HOME", orientdbHome);
			    dBService.start();
			    
				server = vertx.createHttpServer();
				Router router = Router.router(vertx);
	
				// logging
				router.route().handler(LoggerHandler.create());
	
				// JSX template handling
				router.routeWithRegex("/components/.*\\.js").blockingHandler(templateHandler);
//				router.routeWithRegex("/components/.*\\.jsx").blockingHandler(templateHandler);
	
				// handle static js components
				router.route("/components/libs/*").handler(jsxLibsHandler);
				
				// handle static components (anything but JSX currently)
				router.route("/components/*").handler(componentsHandler);
	
				// handle static javascript loads from libs
				router.route("/libs/*").handler(libsHandler);
	
				// handle dynamic data queries
				router.get("/data/topic/byid/:id").blockingHandler(dBService::getTopicByID);
				router.get("/data/topic/byname/:name").blockingHandler(dBService::getTopicByName);
	//			router.get("/data/test").blockingHandler(CatFoodDBService::getTest);
	
				// handle all other content as static files
				router.route("/*").handler(otherHandler);
			
				server.requestHandler(router::accept).listen(port);
			
				future.complete();
			} catch (Exception e) {
				LOGGER.fatal("Failed to initialize CatFood ServerVerticle",e);
				future.fail(e);
			}
		}, res -> {
			if(res.failed()) {
				startFuture.fail(res.cause());
			} else {
				startFuture.complete();
			}
		});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		vertx.executeBlocking(future -> {
			server.close();
			dBService.stop();
			future.complete();
		}, res -> {
			if(res.failed()) {
				stopFuture.fail(res.cause());
			} else {
				stopFuture.complete();
			}
		});
	}

}
