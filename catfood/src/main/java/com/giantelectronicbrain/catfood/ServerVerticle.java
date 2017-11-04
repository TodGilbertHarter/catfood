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

	private IInitializer initializer = InitializerFactory.getInitializer();
	
	private HttpServer server;
	private TestDBService testDBService = (TestDBService) initializer.get(InitializerFactory.TESTDB_SERVICE);
	private String orientdbHome = (String) initializer.get(InitializerFactory.ORIENTDB_HOME);
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
		super.start(startFuture);
		vertx.executeBlocking( future -> {
			server = vertx.createHttpServer();
			Router router = Router.router(vertx);

			// logging
			router.route().handler(LoggerHandler.create());

			// JSX template handling
			router.routeWithRegex("/components/.*\\.jsx").blockingHandler(templateHandler);

			// handle static js components
			router.route("/components/libs/*").handler(jsxLibsHandler);
			
			// handle static components (anything but JSX currently)
			router.route("/components/*").handler(componentsHandler);

			// handle static javascript loads from libs
			router.route("/libs/*").handler(libsHandler);

			// handle dynamic data queries
			router.get("/data/content/:id").blockingHandler(testDBService::getContent);
			router.get("/data/test").blockingHandler(testDBService::getTest);

			// handle all other content as static files
			router.route("/*").handler(otherHandler);
			
			server.requestHandler(router::accept).listen(8080);
			
			try {
			    System.setProperty("ORIENTDB_HOME", orientdbHome);
			    testDBService.start();
				future.complete();
			} catch (Exception e) {
				future.fail(e);
			}
		}, res -> {
			if(res.failed()) {
				startFuture.fail(res.cause());
			}
		});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		vertx.executeBlocking(future -> {
			server.close();
			testDBService.stop();
//			oServer.shutdown();
			future.complete();
		}, res -> {
			if(res.failed()) {
				stopFuture.fail(res.cause());
			}
		});
		super.stop(stopFuture);
	}

}
