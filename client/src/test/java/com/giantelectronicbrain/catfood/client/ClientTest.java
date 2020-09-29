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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.giantelectronicbrain.catfood.client.Client;

/**
 * @author tharter
 *
 */
public class ClientTest {

	/**
	 * Test method for {@link com.giantelectronicbrain.catfood.client.Client#getScripts()}.
	 */
	@Test
	public void testGetScripts() {
		List<String> scripts = Client.getScripts();
		assertNotNull(scripts);
		assertTrue(1<=scripts.size());
		String script = scripts.get(0);
		assertNotNull(script);
		assertTrue(!script.isEmpty());
	}

}
