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

import io.vertx.ext.web.RoutingContext;

/**
 * Service API for handling assets.
 * 
 * @author tharter
 *
 */
public interface IAssetStore {
	public static final String STORE_TYPE_FS = "fs";
	public static final String STORE_TYPE_S3 = "s3";
	
	/**
	 * Given a routing context for a POST operation, transfer
	 * the body of the request to a bucket object. The name of
	 * the object is 
	 * 
	 * @param context
	 */
	public void createAsset(RoutingContext context);
}
