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
package com.giantelectronicbrain.catfood;

import com.giantelectronicbrain.catfood.assets.IAssetStore;
import com.giantelectronicbrain.catfood.initialization.IInitializer;
import com.giantelectronicbrain.catfood.initialization.InitializationException;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

/**
 * Service which handles storing and deleting assets. Note that we
 * assume actually SERVING of assets is not something we need to worry about
 * here. They are S3 buckets, static files, etc. and can be handled by the 
 * web tier.
 * 
 * @author tharter
 *
 */
public class CatFoodAssetService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CatFoodAssetService.class);
	private IInitializer initializer;
	private IAssetStore assetStore;

	/**
	 * Create an asset service backed by the given store.
	 * 
	 * @param assetStore
	 * @throws InitializationException
	 */
	public CatFoodAssetService(IAssetStore assetStore) throws InitializationException {
		this.assetStore = assetStore;
	}

	/**
	 * Create an asset service using the initializer.
	 * 
	 * @throws InitializationException
	 */
	public CatFoodAssetService() throws InitializationException {
		initializer = InitializerFactory.getInitializer();
		this.assetStore = (IAssetStore) initializer.get(InitializerFactory.ASSET_STORE);
	}

	/**
	 * Save a new asset.
	 * 
	 * @param routingContext
	 */
	public void postAsset(final RoutingContext routingContext) {
		this.assetStore.createAsset(routingContext);
	}
	
	/**
	 * Replace an existing asset.
	 * 
	 * @param routingContext
	 */
	public void putAsset(final RoutingContext routingContext) {
		sendResult(routingContext);
	}
	
	/**
	 * Delete an asset.
	 * 
	 * @param routingContext
	 */
	public void deleteAsset(final RoutingContext routingContext) {
		sendResult(routingContext);
	}
	
	/**
	 * 
	 * @param routingContext
	 */
	private void sendResult(final RoutingContext routingContext) {
		routingContext.response()
			.putHeader("content-type","application/json; charset=utf-8")
			.end("");
	}

}
