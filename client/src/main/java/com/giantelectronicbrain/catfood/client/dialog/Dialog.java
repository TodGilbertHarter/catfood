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
package com.giantelectronicbrain.catfood.client.dialog;

import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Div;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.ChunkView;
import com.giantelectronicbrain.catfood.client.facility.Capabilities;
import com.giantelectronicbrain.catfood.client.facility.Facility;
import com.giantelectronicbrain.catfood.client.facility.Signal;
import com.giantelectronicbrain.catfood.client.facility.ViewableComponent;
import com.giantelectronicbrain.catfood.client.facility.ViewableFacility;
import com.giantelectronicbrain.catfood.client.fluent.Css;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.giantelectronicbrain.catfood.client.fluent.Viewable;

import elemental2.dom.Event;

/**
 * @author tharter
 *
 */
public class Dialog extends ViewableFacility {
	private static final Logger logger = Client.PLATFORM.getLogger(Dialog.class.getName());
	private static final String DEFAULT_DIALOG_STYLING = "w3-dialog";
	//Items which are in this menu
	protected ViewableComponent component = null;
	//True if we are actively displaying our contributions (menu items)
	protected boolean active = false;
	// classes to add to the dialog
	protected String styling;
	//Contributions to us attach their fluents here
	protected Fluent contributionPoint;

	/**
	 * Dialog Factory. Create a new Dialog and contribute it to the
	 * given Menu object.
	 * 
	 * @return the created Dialog
	 */
	public static Dialog createDialog(Fluent parent) {
		Dialog dialog = new Dialog(parent,DEFAULT_DIALOG_STYLING);
		return dialog;
	}
	
	/**
	 * Create a menu which is attached to the given fluent, and has the
	 * provided image styling.
	 * 
	 * @param parent Fluent where this is attached.
	 * @param imageStyling classes to add to the rendered menu.
	 */
	public Dialog(Fluent parent, String imageStyling) {
		super(buildCapabilities());
		logger.log(Level.FINEST,"Building a Dialog attached to "+parent);
		this.styling = imageStyling;
		logger.log(Level.FINEST,"WTF IS THE RENDER FUNCTION "+this.getRenderFunction());
		getSignalBroker().registerSlot("ACTIVATE", this::handleActivate);
		viewOn = parent.add(this,this.getRenderFunction());
	}

	/**
	 * Return the fluent which provides the integration point for contributions.
	 * 
	 * @return Fluent which should be the contribution's fluent's parent.
	 */
	public Fluent getUiContributionPoint() {
		return contributionPoint;
	}
	
	/**
	 * Add a Component to this menu. It could be a sub-menu, or anything else
	 * which can participate in a menu.
	 * 
	 * @param item The item to contribute.
	 */
	public ViewableComponent contribute(ViewableComponent component) {
		this.component= component;
		return component;
	}
	
	private void activate(boolean activate) {
		this.active = activate;
		viewOn.sync();
	}
	
	private void activate() {
		activate(!this.active);
	}
	
	protected BiConsumer<Fluent, Event> onClick = (fluent,event) -> {
		logger.log(Level.FINER,"main button was clicked, active is "+active);
		activate();
	};
	
	protected Function<ViewableFacility, Fluent> getRenderFunction() {
		return this::render;
	}

	private Fluent render(ViewableFacility dialog) {
		logger.log(Level.FINEST,"Renderning Dialog "+dialog+", active is: "+active);
		Fluent dialogFluent = Div("dialog w3-modal").click((fluent,event) -> {
			onClick.accept(fluent,event);
		}).css(Css.display, active ? "block" : "none");
		contributionPoint = dialogFluent.div("dialog-content w3-modal-content");
		if(component != null) {
			Viewable display = component.getDisplay();
			if(display instanceof ViewOn<?>) {
				((ViewOn<?>)display).generate(contributionPoint);
			} else {
				contributionPoint.add(display);
			}
		}		
		return dialogFluent;
	}
	
	private static Capabilities buildCapabilities() {
		return new Capabilities(Facility.CAP_EXTENSIBLE);
	}
	
	@Override
	public Set<? extends Facility> getExtensionPoints() {
		return null; //Set.copyOf(this.menus);
	}

	@Override
	public Set<? extends Facility> getExtensionPoints(Capabilities capabilities) {
		return null; // this.menus; //TODO: Fix this...
	}
	
	private void handleActivate(Signal<?,?> activation) {
		logger.log(Level.FINEST,"Handling activate signal context is "+activation.getContext());
		if(component != null) {
			logger.log(Level.FINEST,"A component will now be deleted "+component);
			component.getDisplay().delete();
		}
		component = ChunkView.createChunkView((Chunk)activation.getContext(), this);
		activate(true);
	}

}
