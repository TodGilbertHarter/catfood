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
package com.giantelectronicbrain.catfood.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import elemental2.dom.Event;

/**
 * Javascript Router. This is a simple router which should 
 * provide basic history-based routing capability. I don't
 * think it is necessary anymore to support hash-based
 * routing...
 * 
 * @author tharter
 *
 */
public class Router {
	private static final Logger logger = Client.PLATFORM.getLogger(Router.class.getName());
	private Map<RegExp,Consumer<String[]>> routes = new HashMap<>();
	private String root = "/";
	private String current; // simply holds the current path

	/**
	 * Create a new Router with the given options. Currently only "root" is supported.
	 * 
	 * @param options Map of options.
	 */
	public Router(Map<String,String> options) {
		this.root = options.containsKey("root") ? options.get("root") : "/";
		this.current = root; // we normally start at the root, if not we can navigate somewhere else later...
	}

	/**
	 * Make the router rerun the route handler for the current route.
	 */
	public void kick() {
		this.current = "/supercalafragalisticexpialedocious";
		handleChanged(); // simulate a history event.
	}
	
	/**
	 * Returns the current path. This should always know where we are, although it may not get set when we first enter
	 * the page, I'm not sure...
	 * 
	 * @return the currently active path
	 */
	public String getCurrent() {
		return this.current;
	}

	/**
	 * Add a route with the given path.
	 * 
	 * @param path String the path to add
	 * @param cb callback
	 * @return this
	 */
	public Router add(RegExp pathPattern, Consumer<String[]> cb) {
		this.routes.put(pathPattern, cb);
		return this;
	}
	
	/**
	 * Remove the route at the given path
	 * 
	 * @param path String the path to remove
	 * @return this
	 */
	public Router remove(RegExp pathPattern) {
		this.routes.remove(pathPattern);
		return this;
	}
	
	/**
	 * Clear all routes from this router.
	 * 
	 * @return this
	 */
	public Router flush() {
		routes.clear();
		return this;
	}

	/**
	 * Call pushState and then invoke the changed handler.
	 * 
	 * @param state New object state to set
	 * @param title Browsers ignore this, new page title
	 * @param url The URL to navigate to, this must be relative
	 * @return the router
	 */
	public Router pushState(Object state, String title, String url) {
		Fluent.window.history.pushState(state,title,url);
		handleChanged();
		return this;
	}
	
	/**
	 * Call replaceState and then invoke the changed handler.
	 * 
	 * @param state Replacement state
	 * @param title Browsers ignore this, new page title
	 * @param url The URL to navigate to, this must be relative
	 * @return
	 */
	public Router replaceState(Object state, String title, String url) {
		Fluent.window.history.replaceState(state,title,url);
		handleChanged();
		return this;
	}

	/**
	 * Remove slashes from start and end of path.
	 * 
	 * @param path String path
	 * @return path with slashes removed
	 */
	private String clearSlashes(String path) {
		return path.replaceAll("\\/$", "").replace("^\\/", "");
	}

	/**
	 * Decodes the path into its unencoded form for comparison.
	 * 
	 * @param path the encoded path
	 * @return the decoded path
	 */
	private native String decodeURI(String path) /*-{
		return decodeURI(path);
	}-*/;
	
	private String getFragment() {
		String uri = Fluent.window.location.pathname + Fluent.window.location.search;
		String decoded = decodeURI(uri);
		String fragment = clearSlashes(decoded);
		fragment = fragment.replace("\\?(.*)$", "");
		return (!"/".equals(this.root)) ? fragment.replace(this.root, "") : fragment;
	}
	
	private Router navigate(String path) {
		pushState(null,  null, this.root + this.clearSlashes(path));
		return this;
	}
	
	private void handleChanged() {
		String fragment = this.getFragment();
		logger.log(Level.FINER,"Router handling changed event "+fragment+","+current);
		if(this.current.equals(fragment)) return; // we are already on this page
		this.current = fragment;
		for(RegExp p : routes.keySet()) {
			MatchResult m = p.exec(this.current);
			if(m != null) {
				int gc = m.getGroupCount();
				String[] captures = new String[gc];
				for(int i = 1; i <= gc; i++) {
					captures[i] = m.getGroup(i);
				}
				Consumer<String[]> handler = routes.get(p);
				handler.accept(captures);
				return;
			}
		}
	}
	
	private Router addPopstateListener() {
		Router that = this;
		Fluent.window.addEventListener("popstate", (Event evt) -> {
			that.handleChanged();
		});	
		return this;
	}
}
