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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.client.dialog.Dialog;
import com.giantelectronicbrain.catfood.client.exceptions.SignalError;

/**
 * Helper class which manages signals and slots. Also provides the
 * global signal registering function.
 * 
 * @author tharter
 *
 */
public class SignalBroker {
	private static final Logger logger = Client.PLATFORM.getLogger(SignalBroker.class.getName());
	private final Map<String,Set<Mapping>> signalMap = new HashMap<>();
	private final Map<String,Consumer<Signal<?,?>>> slotMap = new HashMap<>();
	
	private static class Mapping {
		private final String slot;
		private final Component target;
		
		public Mapping(String slot, Component target) {
			if(slot == null || target == null)
				throw new SignalError("A mapping must have a slot and a target");
			this.slot = slot;
			this.target = target;
		}
	}
	
	/**
	 * Wire up a signal between the sender's given signal, and the receiver's given slot. Any signal addressed to the
	 * given slot from the given sender will be delivered to the receiver's handler for that slot.
	 * 
	 * @param signal id of the signal
	 * @param sender Component sending it
	 * @param slot slot it is being sent to
	 * @param target the receiver
	 */
	public static void register(String signal, Component sender, String slot, Component target) {
		logger.log(Level.FINER,"registering signal "+signal+" from "+sender+" for slot "+slot+" on target "+target);
		if(sender == null) throw new SignalError("Sender cannot be null");
		Mapping mapping = new Mapping(slot,target);
		SignalBroker senderSignalBroker = sender.getSignalBroker();
		if(senderSignalBroker == null) throw new SignalError("Failed to get signal broker, cannot register signal");
		senderSignalBroker.registerSignalMapping(signal, mapping);
	}

	private void registerSignalMapping(String signal, Mapping mapping) {
		logger.log(Level.FINER,"Registering signal "+signal+" for Mapping to slot "+mapping.slot+", target is "+mapping.target);
		getSignalSet(signal).add(mapping);
	}

	private void deRegisterSignalMapping(String signal, Mapping mapping) {
//TODO figure this out
	}
	
	/**
	 * Get the handler mapped to a slot.
	 * 
	 * @param slot the slot
	 * @return the handler
	 */
	public Consumer<Signal<?,?>> getHandler(String slot) {
		return slotMap.get(slot);
	}
	
	/**
	 * Dispatch a signal to a given slot. The signal will be delivered to
	 * all registered consumers.
	 * 
	 * @param whichSignal the type of signal we are sending.
	 * @param signal the signal being sent
	 */
	public void dispatch(String whichSignal, Signal<?,?> signal) throws SignalError {
		Set<Mapping> signalSet = getSignalSet(whichSignal);
		if(signalSet == null)
			throw new SignalError("This sender does not have a signal of type "+whichSignal+" registered.");
		boolean foundSlot = false;
		logger.log(Level.FINEST,"WTF is the set size "+signalSet.size());
		for(Mapping mapping : signalSet) {
			logger.log(Level.FINEST,"Trying Mapping "+mapping+", signal slot is "+signal.getSlot()+", mapping slot is "+mapping.slot);
			if(mapping.slot.equals(signal.getSlot())) {
				logger.log(Level.FINEST,"Found a mapping for signal "+whichSignal+" for slot "+signal.getSlot());
				foundSlot = true;
				Component target = mapping.target;
				if(target == null)
					throw new SignalError("A mapping exists, but it is missing a target");
				SignalBroker targetSignalBroker = target.getSignalBroker();
				if(targetSignalBroker == null)
					throw new SignalError("The target's signal broker is missing");
				Consumer<Signal<?,?>> consumer = targetSignalBroker.getHandler(signal.getSlot());
				if(consumer == null)
					throw new SignalError("The mapping for the slot "+signal.getSlot()+" is missing");
				consumer.accept(signal);
			}
		}
		if(!foundSlot)
			logger.log(Level.WARNING,"No handlers were found for signals directed to slot "+signal.getSlot()+" signal was ignored");
	}

	private Set<Mapping> getSignalSet(String signal) {
		Set<Mapping> signalSet = signalMap.get(signal);
		if(signalSet == null) { signalSet = new HashSet<>(); signalMap.put(signal, signalSet); }
		return signalSet;
	}

	/**
	 * Register a handler for a given slot on this broker's owner.
	 * This should really only be called BY the object owning the broker.
	 * 
	 * @param slot
	 * @param target
	 */
	public void registerSlot(String slot, Consumer<Signal<?,?>> handler) {
		logger.log(Level.FINER,"Installing handler for slot "+slot);
		slotMap.put(slot,handler);
	}

	/**
	 * Deregister a handler for a given slot. This should probably
	 * only be called by the broker's owner.
	 * 
	 * @param slot
	 */
	public void deRegisterSlot(String slot) {
		slotMap.remove(slot);
	}
	
}
