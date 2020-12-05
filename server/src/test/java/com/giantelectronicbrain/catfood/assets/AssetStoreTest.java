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

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * @author tharter
 *
 */
@RunWith(VertxUnitRunner.class)
public class AssetStoreTest {

	Vertx vertx;
	int port;
	
	@Before
	public void setUp(TestContext context) throws IOException {
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();
		System.out.println("********************************************************PORT IS "+port);
		
		DeploymentOptions options = new DeploymentOptions()
			.setConfig(new JsonObject().put("http.port",port));
		
		vertx = Vertx.vertx();
		System.out.println("GOT HERE 1");
		vertx.deployVerticle(AssetStoreTestVerticle.class.getName(), options, result -> {
			if(result.succeeded()) {
				System.out.println("VERTICLE IS RUNNING");
			} else {
				Throwable e = result.cause();
				System.out.println("THE STUPID F'ING THING FAILED");
//				e.printStackTrace();
				context.fail(e);
			}
		}); // context.asyncAssertSuccess());
		System.out.println("GOT HERE 2");
	}
	
	@After
	public void tearDown(TestContext context) {
		System.out.println("GOT HERE 5");
		vertx.close(context.asyncAssertSuccess());
	}
	
	@Test
	public void testPost(TestContext context) {
		System.out.println("GOT HERE 3");
		Async async = context.async();
		System.out.println("GOT HERE 4");
		HttpClient client = vertx.createHttpClient();
		Future<HttpClientRequest> requestFut = client.request(HttpMethod.POST, port, null, "/assets/testasset");
		requestFut.onComplete(result -> {
			if(result.succeeded()) {
				HttpClientRequest request = result.result();
				request.send("some test data",response -> {
					if(response.failed()) {
						context.fail("Http POST failed");
						client.close();
						async.complete();
					} else {
						checkForAsset(context);
						client.close();
						async.complete();
					}
				});
			} else {
				Throwable e = result.cause();
				e.printStackTrace();
				context.fail("Failed to create HTTP client request");
				client.close();
				async.complete();
			}
		});
	}

	private void checkForAsset(TestContext context) {
		FileSystem fileSystem = vertx.fileSystem();
		Buffer buffer = fileSystem.readFileBlocking("./build/buckettests/assets/testasset");
		String data = new String(buffer.getBytes());
		context.assertEquals("some test data", data);
	}
}
