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

import java.util.Arrays;
import java.util.Properties;

import com.giantelectronicbrain.catfood.conf.ConfigurationException;
import com.giantelectronicbrain.catfood.conf.Configurator;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;

import io.vertx.core.Vertx;

/**
 * CatFood Application. A Vertx-based content generation system backed up by an OrientDB data store.
 * 
 * @author tharter
 *
 */
public class CatFood {

	/**
	 * Main entry point for the CatFood framework.
	 * CatFood application mainline. Creates configuration properties from command line and configuration file,
	 * configures the CatFood InitializerFactory, creates the root Vertx object, and uses it to deploy the
	 * CatFood ServerVerticle.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Properties configuration = Configurator.createConfiguration(Arrays.asList(args));
			InitializerFactory.setConfiguration(configuration);
			System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
			Vertx vertx = Vertx.vertx();
/*			vertx.deployVerticle("com.giantelectronicbrain.catfood.ServerVerticle",res -> {
				if(res.failed())
					System.exit(-1);
			});
*/			vertx.deployVerticle("com.giantelectronicbrain.catfood.ServerVerticle");
		} catch (ConfigurationException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getHelpMessage());
			System.exit(-1);
		}
	}

}
