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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import io.vertx.core.Vertx;

/**
 * @author tharter
 *
 */
public class ServerRender {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		rtest();
	}

	private static void rtest() {
		System.out.println("WORKING DIR:"+System.getProperty("user.dir"));
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();
		for(URL url : urls) {
			System.out.println(url.getFile());
		}
		Vertx vertx = Vertx.vertx();
		vertx.executeBlocking(future -> {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("nashorn");
			try {
				SimpleBindings bindings = new SimpleBindings();
				engine.eval(read("src/main/javascript/nashorn-require.js"),bindings);
				engine.eval("var initRequire = load('src/main/javascript/nashorn-require.js');",bindings);
				engine.eval("initRequire({mainFile : 'src/main/javascript/foo', debug : true})", bindings);
				engine.eval("var babel = require('babel');",bindings);
				String input = new String(Files.readAllBytes(Paths.get("webroot/components/hello.jsx")),StandardCharsets.UTF_8);
				bindings.put("input",input);
				Object result = engine.eval("babel.transform(input,{ presets: ['react'] }).code;",bindings);
				future.complete(result);
			} catch (Exception e) {
				e.printStackTrace();
				future.fail(e.getLocalizedMessage());
			}
		}, res -> {
			System.out.println("The result is: "+res.result());
			vertx.close();
		});
	}
	
	private static Reader read(String path) throws FileNotFoundException {
	    File file = new File(path);
	    return new BufferedReader(new FileReader(file));
	}

}
