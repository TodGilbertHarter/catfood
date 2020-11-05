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

import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.giantelectronicbrain.catfood.client.chunk.ChunkId;
import com.giantelectronicbrain.catfood.initialization.IInitializer;
import com.giantelectronicbrain.catfood.initialization.InitializationException;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;
import com.giantelectronicbrain.catfood.store.ICatFoodDBStore;
import com.giantelectronicbrain.catfood.store.OrientDBStore;
import com.giantelectronicbrain.catfood.store.StorageException;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Web service adapter for the CatFood test database.
 * 
 * @author tharter
 *
 */
public class CatFoodDBService {
	private IInitializer initializer = InitializerFactory.getInitializer();
	private ICatFoodDBStore catFoodDbStore; // = (ICatFoodDBStore) initializer.get(InitializerFactory.CATFOOD_DB_STORE);
	
	/**
	 * Instantiate the service.
	 * @throws InitializationException 
	 */
	public CatFoodDBService() throws InitializationException {
		catFoodDbStore = (ICatFoodDBStore) initializer.get(InitializerFactory.CATFOOD_DB_STORE);
		}

	/**
	 * Start the service.
	 */
	public void start() {
		this.catFoodDbStore.start();
	}

	/**
	 * Stop the service.
	 */
	public void stop() {
		this.catFoodDbStore.stop();
	}
	
	/**
	 * Given the routing request of a request to search for topics, find all topics
	 * which match a given pattern and return them.
	 * 
	 * @param routingContext
	 */
	public void findTopic(final RoutingContext routingContext) {
		final String pattern = routingContext.request().getParam("pattern");
		try {
			String result = catFoodDbStore.findTopic(pattern);			
			sendResult(routingContext,result);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Given the routing request of a request to delete a CatFood topic, call
	 * the store to perform the task.
	 * 
	 * @param routingContext
	 */
	public void deleteTopicByID(final RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		ChunkId chunkId = new ChunkId(id);
		try {
			catFoodDbStore.deleteContent(chunkId);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendResult(routingContext,null);
	}
	
	/**
	 * Given the routingContext of a request for CatFood content supply that content and
	 * send the results back to the requesting client.
	 * 
	 * @param routingContext the Vert.x routing context of the content request
	 */
	public void getTopicByID(final RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		if(id == null) {
			routingContext.response().setStatusCode(400).end(OrientDBStore.class.getName()+".getContent: no id was supplied");
		} else {
			String result = catFoodDbStore.getJsonContent(new ChunkId(id));
			sendResult(routingContext,result);
		}
	}
	
	public void getTopicByName(final RoutingContext routingContext) {
		final String name = routingContext.request().getParam("name");
		if(name == null) {
			routingContext.response().setStatusCode(400);
				sendResult(routingContext,"<div>"+OrientDBStore.class.getName()+".getTopicByName: no name was supplied</div>");
		} else {
			String result = catFoodDbStore.getJsonContent(name);
			if(result == null) {
				routingContext.response().setStatusCode(404);
				sendResult(routingContext,"<div>"+OrientDBStore.class.getName()+".getTopicByName: no topic was found with the given name</div>");
			} else
				sendResult(routingContext,result);
		}
	}
	
	public void saveTopicByName(final RoutingContext routingContext) {
		routingContext.request().bodyHandler(bodyHandler -> {
			final JsonObject body = bodyHandler.toJsonObject();
			Chunk chunk = new Chunk(body.getString("content"), null, body.getString("name"), Language.valueOf(body.getString("lang")), body.getLong("lastUpdated"));
			ChunkId result = catFoodDbStore.postContent(chunk);
			String resultJson = new JsonObject().put("@RID", result.getChunkId()).encode();
			sendResult(routingContext,resultJson);
		});
	}
	
	public void updateTopicById(final RoutingContext routingContext) {
		routingContext.request().bodyHandler(bodyHandler -> {
			final JsonObject body = bodyHandler.toJsonObject();
			Chunk chunk = new Chunk(body.getString("content"), null, body.getString("name"), Language.valueOf(body.getString("lang")), body.getLong("lastUpdated"));
			ChunkId chunkId = new ChunkId(body.getString("@rid"));
			chunk.setChunkId(chunkId);
			try {
				catFoodDbStore.putContent(chunk);
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendResult(routingContext,null);
		});
	}

	private void sendResult(final RoutingContext routingContext, final String json) {
		routingContext.response()
			.putHeader("content-type","application/json; charset=utf-8")
			.end(json == null ? "" : json);
		
	}
}
