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
package com.giantelectronicbrain.catfood.client.asset;

import java.util.function.BiConsumer;

import javax.naming.OperationNotSupportedException;

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * @author tharter
 *
 */
public class Repository {

	private String POST_ASSET = "/assetstore/";
	private String PUT_ASSET = "/assetstore/";
	private String DELETE_ASSET = "/assetstore/";
	
	public void postAsset(String formId, BiConsumer<Integer,String> handler) {
		sendAsset("POST",POST_ASSET,handler,formId);
	}
	
	public void putAsset(String formId, BiConsumer<Integer,String> handler) {
		sendAsset("PUT",PUT_ASSET,handler,formId);
	}
	
	public void deleteAsset(String assetId, BiConsumer<Integer,String> handler) {
		throw new Error("not yet implemented");
	}
	
	private void sendAsset(String method, String query, BiConsumer<Integer,String> handler, String requestData) {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(a -> {
			if(handler == null || xhr.getReadyState() != 4) {
				return;
			}
			String result = null;
			if(xhr.getStatus() == 200) {
				result = xhr.getResponseText();
			}
			handler.accept(xhr.getStatus(), result);
		});
		
		xhr.open(method, query);
		xhr.send(requestData);
	}

}
