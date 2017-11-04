/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
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

package com.giantelectronicbrain.catfood.initialization;

import java.io.File;
import java.util.Properties;

import com.giantelectronicbrain.catfood.TestDBService;
import com.giantelectronicbrain.catfood.templ.JSXTemplateEngine;

import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.TemplateEngine;

/**
 * Static configuration factory. 
 * 
 * @author tharter
 *
 */
public class InitializerFactory {
	public static final String ORIENTDB_HOME = "com.giantelectronicbrain.catfood.orientdbhome";
	public static final String TESTDB_SERVICE = "com.giantelectronicbrain.catfood.testdbservice";
	public static final String JSX_TEMPLATEHANDLER = "com.giantelectronicbrain.catfood.jsxtemplatehandler";
	public static final String JSX_LIBSHANDLER = "com.giantelectronicbrain.catfood.jsxlibshandler";
	public static final String STATIC_HANDLER = "com.giantelectronicbrain.catfood.statichandler";
	public static final String LIBS_HANDLER = "com.giantelectronicbrain.catfood.libshandler";
	public static final String COMPONENTS_HANDLER = "com.giantelectronicbrain.catfood.componentshandler";
	public static final String WEBROOT = "com.giantelectronicbrain.catfood.webroot";
	
	private static volatile IInitializer initializerInstance;
	private static volatile Properties configuration;
	
	public static synchronized void setConfiguration(Properties newConfiguration) {
		configuration = newConfiguration;
	}
	
	/**
	 * Initialize the system global state. If it doesn't exist, create it. This is
	 * thread safe.
	 * 
	 * @param configuration Properties object containing the desired configuration
	 * @return IInitializer containing system's global configuration
	 */
	public static synchronized IInitializer getInitializer() {
		if(initializerInstance == null) {
			initializerInstance = initialize(configuration);
		}
		return initializerInstance;
	}
	
	/**
	 * Create an empty IInitializer. 
	 * 
	 * @return empty configurator
	 */
	private static IInitializer createInitializer() {
		IInitializer config = new InitializerImpl();
		return config;
	}
	
	/**
	 * Populate a configurator with our system configuration. 
	 * 
	 * @param config empty IInitializer
	 * @return populated IInitializer
	 */
	private static IInitializer initialize(Properties config) {
		IInitializer initializer = createInitializer();
		
		initializer.set(TESTDB_SERVICE, new TestDBService());

		String webroot = "webroot";
		initializer.set(WEBROOT, webroot);
		
		String orientdbHome = new File("orientdb").getAbsolutePath(); //Set OrientDB home to current directory
		initializer.set(ORIENTDB_HOME,orientdbHome);

		TemplateEngine jsxTemplateEngine = JSXTemplateEngine.create();
		initializer.set(JSX_TEMPLATEHANDLER, TemplateHandler.create(jsxTemplateEngine, webroot, "text/javascript"));

		initializer.set(STATIC_HANDLER,StaticHandler.create().setWebRoot(webroot+"/content").setIncludeHidden(false).setDirectoryListing(false).setCacheEntryTimeout(1).setMaxAgeSeconds(1).setCachingEnabled(false));
		initializer.set(LIBS_HANDLER,StaticHandler.create().setWebRoot(webroot+"/libs").setIncludeHidden(false).setDirectoryListing(false).setCacheEntryTimeout(1).setMaxAgeSeconds(1));
		
		TemplateEngine jsTemplateEngine = JSXTemplateEngine.create().setExtension("js");
		initializer.set(JSX_LIBSHANDLER,TemplateHandler.create(jsTemplateEngine,webroot+"/components/lib","text/javascript"));

		initializer.set(COMPONENTS_HANDLER,StaticHandler.create().setWebRoot(webroot+"/components").setIncludeHidden(false).setDirectoryListing(false).setCacheEntryTimeout(1).setMaxAgeSeconds(1));
		return initializer;
	}
}
