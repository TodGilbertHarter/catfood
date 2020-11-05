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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tharter
 *
 */
public class SignalBrokerTest {

	private SignalBroker senderBroker;
	private SignalBroker receiverBroker;

	private Signal<?,?> receivedSignal = null;
	
	private void handler(Signal<?,?> signal) {
		this.receivedSignal = signal;
	}
	
	@Before
	public void setUp() {
		senderBroker = new SignalBroker();
		receiverBroker = new SignalBroker();
		receivedSignal = null;
	}
	
	@Test
	public void testRegisterSlot() {
		receiverBroker.registerSlot("SLOT", this::handler);
		Consumer<Signal<?,?>> handler = receiverBroker.getHandler("SLOT");
		assertNotNull(handler);
	}

	private class SignalTester implements Component {
		private SignalBroker broker = new SignalBroker();

		@Override
		public SignalBroker getSignalBroker() {
			return this.broker;
		}
	}
	
	
	@Test
	public void testRegister() {
		SignalTester testSender = new SignalTester();
		SignalTester testTarget = new SignalTester();
		testTarget.getSignalBroker().registerSlot("SLOT", this::handler);
		SignalBroker.register("SIGNAL", testSender, "SLOT", testTarget);
		
		Signal testSignal = new Signal<String,String>("SLOT","INTENT","CONTEXT");
		testSender.getSignalBroker().dispatch("SIGNAL", testSignal);
		assertEquals(this.receivedSignal,testSignal);
	}
}
