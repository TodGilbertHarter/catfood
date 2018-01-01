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

package com.giantelectronicbrain.catfood.templ.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import com.giantelectronicbrain.catfood.initialization.IInitializer;
import com.giantelectronicbrain.catfood.initialization.InitializationException;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;
import com.giantelectronicbrain.catfood.templ.JSXTemplateEngine;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.impl.CachingTemplateEngine;

/**
 * Provides a caching JSX service engine. JSX modules are loaded from disk, transpiled to 
 * ES5, and the results cached. This complies with the Vert.x TemplateEngine API, allowing
 * Vert.x routers to serve up JSX files to clients as Javascript. Note that the engine
 * should be configured to output MIME type 'text/javascript'.
 * 
 * @author tharter
 *
 */
public class JSXTemplateEngineImpl extends CachingTemplateEngine<ProcessedJSX> implements JSXTemplateEngine {
	private static final Logger LOGGER = LoggerFactory.getLogger(JSXTemplateEngineImpl.class);
	
	private IInitializer initializer; // = InitializerFactory.getInitializer();
	
	private String scriptBase; // = (String) initializer.get(InitializerFactory.SCRIPTBASE);
	private ScriptEngineManager factory;
	private ScriptEngine engine;
	private SimpleBindings bindings;

	/*
	 * Initialize the engine. Note that I still need to find a way to make the initialization non-blocking, as
	 * currently starting up Nashorn and compiling Babel is relatively time-consuming.
	 * TODO: Make this push the time-consuming part into another thread or a background operation.
	 */
	public JSXTemplateEngineImpl() throws InitializationException {
		super(DEFAULT_TEMPLATE_EXTENSION, DEFAULT_MAX_CACHE_SIZE);

		initializer = InitializerFactory.getInitializer();
		scriptBase = (String) initializer.get(InitializerFactory.SCRIPTBASE);
		
		try {
			factory = new ScriptEngineManager();
			engine = factory.getEngineByName("nashorn");
			bindings = new SimpleBindings();
//			engine.eval(reader("src/main/javascript/nashorn-require.js"),bindings);
//			engine.eval("var initRequire = load('src/main/javascript/nashorn-require.js');",bindings);
			engine.eval("var initRequire = load('"+scriptBase+"/nashorn-require.js');",bindings);
			engine.eval("var Marker = Java.type(\"com.giantelectronicbrain.catfood.CatFood\"); var options = {mainFile : '"+scriptBase+"/foo.js', debug : true,"+
					" ClassLoader: new Marker().getClass().getClassLoader()};",bindings);
			engine.eval("initRequire(options)", bindings);
//			engine.eval("var babel = require('"+scriptBase+"/babel.js');",bindings);
			engine.eval("var babel = require('babel.js');",bindings);
			
		} catch (Exception e) {
			LOGGER.error("Failed to initialize nashorn",e);
		}
	}

	@Override
	public JSXTemplateEngine setExtension(String extension) {
		doSetExtension(extension);
		return this;
	}

	@Override
	public JSXTemplateEngine setMaxCacheSize(int maxCacheSize) {
		this.cache.setMaxSize(maxCacheSize);
		return this;
	}

	@Override
	public void render(RoutingContext context, String templateDirectory, String templateFileName,
			Handler<AsyncResult<Buffer>> handler) {
		render(context,templateDirectory+"/"+templateFileName,handler);
	}
	
	@Override
	public void render(RoutingContext context, String templateFileName, Handler<AsyncResult<Buffer>> handler) {
		try {
			ProcessedJSX template = cache.get(templateFileName);
			if(LOGGER.isTraceEnabled())
				LOGGER.trace("Cache got a hit for "+templateFileName+" of "+template);
			if (template == null) {
				if(LOGGER.isTraceEnabled())
					LOGGER.trace("Didn't find "+templateFileName+" in the cache, loading");
				template = processJSX(adjustLocation(templateFileName),context);
				if(template != null)
					cache.put(templateFileName, template);
				else
					throw new Exception("Failed to parse template "+templateFileName);
			} else {
				long milliseconds = getFileModified(adjustLocation(templateFileName),context);
				if(milliseconds > template.lastModified) {
					if(LOGGER.isTraceEnabled())
						LOGGER.trace("Cached version was from "+template.lastModified+", file system mod time is "+milliseconds+", not loading from cache");
					template = processJSX(adjustLocation(templateFileName),context);
					cache.put(templateFileName, template);
				}
			}
			handler.handle(Future.succeededFuture(Buffer.buffer(template.source)));
		
		} catch (Exception ex) {
			handler.handle(Future.failedFuture(ex));
		}
	}

	@Override
	protected String adjustLocation(String location) {
		String superLocation = super.adjustLocation(location);
		superLocation += "x";
		LOGGER.debug("Adjusting location from "+location+", to "+superLocation);
		return superLocation;
	}

	private long getFileModified(String location, RoutingContext context) {
		Vertx vertx = context.vertx();
		return vertx.fileSystem().propsBlocking(location).lastModifiedTime();
	}

	private ProcessedJSX processJSX(String fileLocation, RoutingContext context) {
		Object result = null;
		try {
			LOGGER.debug("Tring to locate file at location "+fileLocation);
			Buffer input = findTemplateSource(fileLocation,context);
			if(input != null) {
				bindings.put("input",input.toString());
				result = engine.eval("babel.transform(input,{ presets: ['react', 'es2015'], plugins: ['transform-es2015-classes'] }).code;",bindings);
//				result = engine.eval("babel.transform(input,{ presets: ['react', 'es2015'] }).code;",bindings);
			}
		} catch (Exception e) {
			LOGGER.warn("Failed to parse JSX "+e.getLocalizedMessage(),e);
		}
		return result == null ? null : new ProcessedJSX(fileLocation, ((String)result).getBytes(), System.currentTimeMillis());
	}

	private static Reader reader(String path) throws FileNotFoundException {
	    File file = new File(path);
	    return new BufferedReader(new FileReader(file));
	}

	private Buffer findTemplateSource(String name, RoutingContext context) throws IOException {
	    try {
	    	Vertx vertx = context.vertx();
	      // check if exists on file system
	      if (vertx.fileSystem().existsBlocking(name)) {
	        return vertx.fileSystem().readFileBlocking(name);
	      } else {
	    	LOGGER.info("Couldn't find template file "+name);
	        return null;
	      }

	    } catch (Exception e) {
	      throw new IOException(e);
	    }
	  }


}
