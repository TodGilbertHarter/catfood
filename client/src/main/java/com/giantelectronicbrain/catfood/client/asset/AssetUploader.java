/**
 * This software is Copyright (C) 2021 Tod G. Harter. All rights reserved.
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

import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.ChunkView;
import com.giantelectronicbrain.catfood.client.facility.SignalBroker;
import com.giantelectronicbrain.catfood.client.facility.ViewableComponent;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.giantelectronicbrain.catfood.client.fluent.Viewable;

/**
 * @author tharter
 *
 */
public class AssetUploader implements ViewableComponent {
	private final SignalBroker signalBroker = new SignalBroker();
	private static final Logger logger = Client.PLATFORM.getLogger(ChunkView.class.getName());
	private Fluent root;			// The root object where our UI lives
	private Fluent view;		// The Viewable which is displaying the UI

	@Override
	public SignalBroker getSignalBroker() {
		return signalBroker;
	}

	@Override
	public Viewable getDisplay() {
		return this.view;
	}

	public void render() {
		view = root.div();
		Fluent form = view.form().id("assetloaderform");
		form.input("uploadfile", "file");
		form.button("uploadsubmit", "button", "upload").click(
			(target,event) -> {
				//TODO: invoke repository to do upload...
			});
		
	}
}
