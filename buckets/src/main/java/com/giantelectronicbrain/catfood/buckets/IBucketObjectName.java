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

package com.giantelectronicbrain.catfood.buckets;

/**
 * The name of an object in a bucket. Providers will implement
 * provider specific versions of this to implement their semantics.
 * 
 * @author tharter
 *
 */
public interface IBucketObjectName {

	/**
	 * Get the string value of the bucket object's name
	 * 
	 * @return string bucket name
	 */
	public String getName();
	
	/**
	 * The name of the bucket this object resides within.
	 * 
	 * @return the object's parent bucket's name
	 */
	public IBucketName getBucketName();
}
