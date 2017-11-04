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

package com.giantelectronicbrain.catfood.templ;

import com.giantelectronicbrain.catfood.templ.impl.JSXTemplateEngineImpl;

import io.vertx.ext.web.templ.TemplateEngine;

/**
 * @author tharter
 *
 */
public interface JSXTemplateEngine extends TemplateEngine {
	  /**
	   * Default max number of templates to cache
	   */
	  int DEFAULT_MAX_CACHE_SIZE = 10000;

	  /**
	   * Default template extension
	   */
	  String DEFAULT_TEMPLATE_EXTENSION = "jsx";

	  /**
	   * Create a template engine using defaults
	   *
	   * @return  the engine
	   */
	  static JSXTemplateEngine create() {
	    return new JSXTemplateEngineImpl();
	  }

	  /**
	   * Set the extension for the engine
	   *
	   * @param extension  the extension
	   * @return a reference to this for fluency
	   */
	  JSXTemplateEngine setExtension(String extension);

	  /**
	   * Set the max cache size for the engine
	   *
	   * @param maxCacheSize  the maxCacheSize
	   * @return a reference to this for fluency
	   */
	  JSXTemplateEngine setMaxCacheSize(int maxCacheSize);

}
