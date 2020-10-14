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
package com.giantelectronicbrain.catfood.client.facility;

/**
 * Class which encapsulates a signal.
 * 
 * @author tharter
 *
 */
public class Signal<S, T> {
	private final String slot;
	private final S intent;
	private final T context;
	
	/**
	 * Construct a signal with an intent and context of the given types.
	 * 
	 * @param signal the name of the signal being dispatched
	 * @param intent the intent of the sender in sending the signal
	 * @param context any context needed to interpret the signal
	 */
	public Signal(String slot, S intent, T context) {
		this.slot = slot;
		this.intent = intent;
		this.context = context;
	}
	/**
	 * @return the signal
	 */
	public String getSlot() {
		return slot;
	}

	/**
	 * @return the intent
	 */
	public S getIntent() {
		return intent;
	}

	/**
	 * @return the context
	 */
	public T getContext() {
		return context;
	}

}
