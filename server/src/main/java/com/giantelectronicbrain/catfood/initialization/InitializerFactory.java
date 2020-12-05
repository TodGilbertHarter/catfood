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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.giantelectronicbrain.catfood.CatFoodAssetService;
import com.giantelectronicbrain.catfood.CatFoodDBService;
import com.giantelectronicbrain.catfood.assets.FSAssetStore;
import com.giantelectronicbrain.catfood.assets.IAssetStore;
import com.giantelectronicbrain.catfood.store.OrientDBStore;

import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
//import io.vertx.ext.web.templ.TemplateEngine;

/**
 * Static configuration factory. 
 * 
 * @author tharter
 *
 */
public class InitializerFactory {
	public static final String WRITE = "write";
	public static final String CATFOOD_DB_SERVICE = "com.giantelectronicbrain.catfood.testdbservice";
	public static final String CATFOOD_DB_STORE = "com.giantelectronicbrain.catfood.dbstore";
	public static final String ORIENTDB_HOME = "com.giantelectronicbrain.catfood.orientdbhome";
	public static final String ORIENTDB_URL = "com.giantelectronicbrain.catfood.orientdburl";
	public static final String ORIENTDB_USER = "com.giantelectronicbrain.catfood.orientdbuser";
	public static final String ORIENTDB_PASSWORD = "com.giantelectronicbrain.catfood.orientdbpassword";
	public static final String ORIENTDB_DATABASE = "com.giantelectronicbrain.catfood.orientdatabase";
	public static final String JSX_LIBSHANDLER = "com.giantelectronicbrain.catfood.jsxlibshandler";
	public static final String STATIC_HANDLER = "com.giantelectronicbrain.catfood.statichandler";
	public static final String LIBS_HANDLER = "com.giantelectronicbrain.catfood.libshandler";
	public static final String COMPONENTS_HANDLER = "com.giantelectronicbrain.catfood.componentshandler";
	public static final String WEBROOT = "com.giantelectronicbrain.catfood.webroot";
	public static final String PORT = "com.giantelectronicbrain.catfood.port";
	public static final String SCRIPTBASE = "com.giantelectronicbrain.catfood.scriptbase";
	public static final String CLIENT_DEBUG = "com.giantelectronicbrain.catfood.clientdebug";
	public static final String ASSET_STORE = "com.giantelectronicbrain.catfood.assetstore";
	public static final String ASSET_STORE_SERVICE = "com.giantelectronicbrain.catfood.assetstore.service";
	public static final String ASSET_STORE_TYPE = "com.giantelectronicbrain.catfood.assetstore.type";
	public static final String ASSET_STORE_LOCATION = "com.giantelectronicbrain.catfood.assetstore.location";
	public static final String ASSET_STORE_CREDENTIALS = "com.giantelectronicbrain.catfood.assetstore.credentials";
	
	private static volatile IInitializer initializerInstance;
	private static volatile Properties configuration;
	private static volatile FileSystem fileSystem;
	
	public static synchronized void setConfiguration(Properties newConfiguration) {
		configuration = newConfiguration;
	}
	
	public static synchronized void setFileSystem(FileSystem aFileSystem) {
		fileSystem = aFileSystem;
	}
	
	/**
	 * Initialize the system global state. If it doesn't exist, create it. This is
	 * thread safe.
	 * 
	 * @param configuration Properties object containing the desired configuration
	 * @return IInitializer containing system's global configuration
	 * @throws InitializationException if initialization cannot be performed successfully
	 */
	public static synchronized IInitializer getInitializer() throws InitializationException {
		if(initializerInstance == null) {
			initializerInstance = createInitializer();
			initialize(configuration);
			if("true".equals(configuration.getProperty(WRITE)))
				write();
		}
		return initializerInstance;
	}
	
	/**
	 * Create an empty IInitializer. 
	 * 
	 * @return empty IInitializer
	 */
	private static IInitializer createInitializer() {
		IInitializer config = new InitializerImpl();
		return config;
	}
	
	/**
	 * Write a copy of the 
	 * @param config
	 */
	private static void write() {
		initializerInstance.print();
	}
	
	/**
	 * Populate the Initializer with our system initialized state.
	 * 
	 * @param config The configuration for this initialization
	 * @throws InitializationException if a component cannot initialize
	 *
	 */
	private static void initialize(Properties config) throws InitializationException {
		/**
		 * Note: Things need to be initialized in dependency order here, because when a component
		 * which itself relies on other components (IE calls initializer.get(key)) then it will fail
		 * unless its dependencies were already initialized (because it will be looking them up in the
		 * same initializer we are setting up here). The main disadvantage of this is order may be 
		 * dependent on implementation of some of these objects, thus leaking implementation side-effects
		 * back here. Oh well...
		 */
		
		// Initialize OrientDB service.
		String orientdbHome = new File(config.getProperty(ORIENTDB_HOME,"../orientdb")).getAbsolutePath();
		initializerInstance.set(ORIENTDB_HOME,orientdbHome);
		String orientDatabase = config.getProperty(ORIENTDB_DATABASE,"CatFood");
		initializerInstance.set(ORIENTDB_DATABASE,orientDatabase);
		initializerInstance.set(ORIENTDB_URL, "plocal:/"+orientdbHome+"/databases/");
		String orientdbUser = config.getProperty(ORIENTDB_USER,"admin");
		initializerInstance.set(ORIENTDB_USER, orientdbUser);
		String orientdbPassword = config.getProperty(ORIENTDB_PASSWORD,"admin");
		initializerInstance.set(ORIENTDB_PASSWORD, orientdbPassword);
		initializerInstance.set(CATFOOD_DB_STORE, new OrientDBStore()); //TODO: make the class configurable
		initializerInstance.set(CATFOOD_DB_SERVICE, new CatFoodDBService());
		
		// Initialize asset store
		String assetStoreType = config.getProperty(ASSET_STORE_TYPE);
		IAssetStore assetStore = null;
		if(IAssetStore.STORE_TYPE_FS.equals(assetStoreType)) {
			String basePath = config.getProperty(ASSET_STORE_LOCATION);
			assetStore = new FSAssetStore(basePath,fileSystem);
			initializerInstance.set(ASSET_STORE, assetStore);
		} else if(IAssetStore.STORE_TYPE_S3.equals(assetStoreType)){
			//TODO: s3 type store
			assetStore = null;
		}
		CatFoodAssetService assetService = new CatFoodAssetService();

		// Initialize handlers
		initializerInstance.set(SCRIPTBASE, config.getProperty(SCRIPTBASE,"../javascript"));
		initializerInstance.set(CLIENT_DEBUG, Boolean.parseBoolean(config.getProperty(CLIENT_DEBUG,"false")));
		Integer port = Integer.parseUnsignedInt(config.getProperty(PORT,"8080"));
		initializerInstance.set(PORT, port);
		String webroot = config.getProperty(WEBROOT,"webroot");
		initializerInstance.set(WEBROOT, webroot);		

		initializerInstance.set(STATIC_HANDLER,StaticHandler.create().setWebRoot(webroot+"/content").setIncludeHidden(false).setDirectoryListing(false).setCacheEntryTimeout(1).setMaxAgeSeconds(1).setCachingEnabled(false).setIndexPage("index.html"));
		initializerInstance.set(LIBS_HANDLER,StaticHandler.create().setWebRoot(webroot+"/libs").setIncludeHidden(false).setDirectoryListing(false).setCacheEntryTimeout(1).setMaxAgeSeconds(1));
		
		initializerInstance.set(COMPONENTS_HANDLER,StaticHandler.create().setWebRoot(webroot+"/components").setIncludeHidden(false).setDirectoryListing(false).setCacheEntryTimeout(1).setMaxAgeSeconds(1));
	}
}
