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
import com.giantelectronicbrain.catfood.initialization.InitializationException;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;
import com.giantelectronicbrain.catfood.model.ChunkId;
import com.giantelectronicbrain.catfood.store.ICatFoodDBStore;
import com.giantelectronicbrain.catfood.store.OrientDBStore;

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
	
/*	public void getTest(final RoutingContext routingContext) {
		String result = catFoodDbStore.getTest();
		sendResult(routingContext,result);
	} */
	
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
			String result = catFoodDbStore.getJsonTopic(name);
			if(result == null) {
				routingContext.response().setStatusCode(404);
				sendResult(routingContext,"<div>"+OrientDBStore.class.getName()+".getTopicByName: no topic was found with the given name</div>");
			} else
				sendResult(routingContext,result);
		}
	}
	
	private void sendResult(final RoutingContext routingContext, final String json) {
		routingContext.response()
			.putHeader("content-type","application/json; charset=utf-8")
			.end(json);
		
	}
}
