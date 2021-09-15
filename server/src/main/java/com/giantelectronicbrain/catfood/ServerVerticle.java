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

//import com.giantelectronicbrain.catfood.Client;
import com.giantelectronicbrain.catfood.compiler.GWTCompiler;
import com.giantelectronicbrain.catfood.exceptions.CatfoodApplicationException;
import com.giantelectronicbrain.catfood.exceptions.ErrorResult;
import com.giantelectronicbrain.catfood.exceptions.ExceptionIds;
import com.giantelectronicbrain.catfood.initialization.IInitializer;
import com.giantelectronicbrain.catfood.initialization.InitializationException;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Verticle which acts as a CatFood HTTP server.
 * 
 * @author tharter
 *
 */
@Slf4j
public class ServerVerticle extends AbstractVerticle {

//	private static final Logger LOGGER = LoggerFactory.getLogger(ServerVerticle.class);
	private IInitializer initializer = InitializerFactory.getInitializer();
	
	private HttpServer server;
	private CatFoodDBService dBService; // = (CatFoodDBService) initializer.get(InitializerFactory.CATFOOD_DB_SERVICE);
	private String orientdbHome; // = (String) initializer.get(InitializerFactory.ORIENTDB_HOME);
	private Integer port; // = (Integer) initializer.get(InitializerFactory.PORT);
//	private TemplateHandler templateHandler; // = (TemplateHandler) initializer.get(InitializerFactory.JSX_TEMPLATEHANDLER);
	private StaticHandler otherHandler; // = (StaticHandler) initializer.get(InitializerFactory.STATIC_HANDLER);
	private StaticHandler libsHandler; // = (StaticHandler) initializer.get(InitializerFactory.LIBS_HANDLER);
//	private TemplateHandler jsxLibsHandler; // = (TemplateHandler) initializer.get(InitializerFactory.JSX_LIBSHANDLER);
	private StaticHandler componentsHandler; // = (StaticHandler) initializer.get(InitializerFactory.COMPONENTS_HANDLER);
	private Boolean debugClient;
	private String assetStoreLocation;
	private CatFoodAssetService assetService;
	
	/**
	 * Instantiate the CatFood HTTP Server Verticle.
	 * 
	 * @throws InitializationException 
	 */
	public ServerVerticle() throws InitializationException {
		log.trace("Catfood ServerVerticle initializing");
		dBService = (CatFoodDBService) initializer.get(InitializerFactory.CATFOOD_DB_SERVICE);
		assetService = (CatFoodAssetService) initializer.get(InitializerFactory.ASSET_STORE_SERVICE);
		orientdbHome = (String) initializer.get(InitializerFactory.ORIENTDB_HOME);
		port = (Integer) initializer.get(InitializerFactory.PORT);
		otherHandler = (StaticHandler) initializer.get(InitializerFactory.STATIC_HANDLER);
		libsHandler = (StaticHandler) initializer.get(InitializerFactory.LIBS_HANDLER);
//		jsxLibsHandler = (TemplateHandler) initializer.get(InitializerFactory.JSX_LIBSHANDLER);
		componentsHandler = (StaticHandler) initializer.get(InitializerFactory.COMPONENTS_HANDLER);
		debugClient = (Boolean) initializer.get(InitializerFactory.CLIENT_DEBUG);
		assetStoreLocation = (String) initializer.get(InitializerFactory.ASSET_STORE_LOCATION);
		log.trace("Catfood ServerVerticle initialized");
	}

	@Override
	public void start(Promise<Void> startFuture) throws Exception {
		log.trace("Catfood ServerVerticle starting");
	    System.setProperty("ORIENTDB_HOME", orientdbHome);

	    dBService.start();
	    
		server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		Route all = router.route();
		all.failureHandler(routingContext -> {
			int statusCode = routingContext.statusCode();
			Throwable cause = routingContext.failure();
			ErrorResult result = null;
			if(cause == null) {
				ExceptionIds exid = ExceptionIds.fromStatusCode(statusCode);
				result = ErrorResult.builder().exceptionId(exid)
						.message(exid.getMessage()).build();
			} else if(cause instanceof CatfoodApplicationException) {
				CatfoodApplicationException cae = (CatfoodApplicationException) cause;
				result = ErrorResult.builder()
						.exceptionId(cae.getExceptionId())
						.details(cae.getDetails())
						.message(cae.getMessage())
						.build();
			} else {
				result = ErrorResult.builder()
						.exceptionId(ExceptionIds.fromStatusCode(statusCode))
						.message(cause.getMessage())
						.build();
			}
			String json = JsonObject.mapFrom(result).encode();
			routingContext.end(json);
		});

		// logging
		router.route().handler(LoggerHandler.create());

		// handle static js components
//		router.route("/components/libs/*").handler(jsxLibsHandler);
		
		// handle static components (anything but JSX currently)
		router.route("/components/*").handler(componentsHandler);

		// handle static javascript loads from libs
		router.route("/libs/*").handler(libsHandler);

		// handle dynamic data queries
		router.get("/data/chunk/byid/:id").blockingHandler(dBService::getTopicByID);
		router.get("/data/chunk/byname/:name").blockingHandler(dBService::getTopicByName);
		router.post("/data/chunk").blockingHandler(dBService::saveTopicByName);
		router.put("/data/chunk").blockingHandler(dBService::updateTopicById);
		router.delete("/data/chunk/byid/:id").blockingHandler(dBService::deleteTopicByID);
		router.get("/data/chunk/like/:pattern").blockingHandler(dBService::findTopic);

		// handle asset management/access queries
		router.get("/assetstore/*").handler(getAssetHandler());
		router.post("/assetstore/:name").handler(assetService::postAsset);
		router.put("/assetstore/:name").handler(assetService::putAsset);
		router.delete("/assetstore/:name").handler(assetService::deleteAsset);
				
		// handle webjars, these are located in the classpath
		StaticHandler webjarStaticHandler = StaticHandler.create("META-INF/resources/webjars");
		router.route("/webjars/*").handler(webjarStaticHandler);

		FaviconHandler fih = FaviconHandler.create(vertx);
		router.get("/favicon.ico").handler(fih);
		
		GWTCompiler.folderSource = "client/src/main/java";
		GWTCompiler.setTargetFolder("webroot/content");
//		Handler<RoutingContext> gwtHandler = GWTCompiler.with(Client.class.getName(), "/", debugClient, true, vertx);
		Handler<RoutingContext> gwtHandler = GWTCompiler.with("com.giantelectronicbrain.catfood.client.Client", "/", debugClient, true, vertx);
		router.get("/").handler(gwtHandler);
		// in case a client loads one of the internal routes, then give them the main page
		router.get("/edit/*").handler(context -> context.reroute("/")); 
		router.get("/view/*").handler(context -> context.reroute("/"));
		router.get("/find/name/*").handler(context -> context.reroute("/"));
		router.get("/*").handler(otherHandler);
		
		server.requestHandler(req -> router.handle(req));
		log.trace("Catfood ServerVerticle setup complete, going to start listening on port {}",port);
		server.listen(port, "0.0.0.0", res -> {
			if(res.succeeded()) {
				startFuture.complete();
				log.trace("Catfood is now Listening");
			} else {
				startFuture.fail(res.cause());
				log.error("Catfood failed to listen",res.cause());
			}
		});
			
	}
	
	private Handler<RoutingContext> getAssetHandler() {
		return StaticHandler.create(assetStoreLocation); //NOTE: this will only be useful if assets are locally stored
	}

	@Override
	public void stop(Promise<Void> stopFuture) throws Exception {
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
