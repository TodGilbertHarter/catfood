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

import com.giantelectronicbrain.catfood.store.TestDB;

import io.vertx.ext.web.RoutingContext;

/**
 * Web service adapter for the CatFood test database.
 * 
 * @author tharter
 *
 */
public class TestDBService {

	private TestDB testDB;
	
	/**
	 * 
	 */
	public TestDBService() {
		this.testDB = new TestDB();
	}

	public void start() {
		this.testDB.start();
	}
	
	public void stop() {
		this.testDB.stop();
	}
	
	public void getTest(final RoutingContext routingContext) {
		String result = testDB.getTest();
		sendResult(routingContext,result);
	}
	
	public void getContent(final RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		if(id == null) {
			routingContext.response().setStatusCode(400).end(TestDB.class.getName()+".getContent: no id was supplied");
		} else {
			String result = testDB.getContent(id);
			sendResult(routingContext,result);
		}
	}
	
	private void sendResult(final RoutingContext routingContext, final String json) {
		routingContext.response()
			.putHeader("content-type","application/json; charset=utf-8")
			.end(json);
		
	}
}
