/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
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

package com.giantelectronicbrain.catfood.initialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.giantelectronicbrain.catfood.SeparateClassloaderTestRunner;

/**
 * Note that this effectively tests Initializer as well, so we will not bother to 
 * provide a whole separate test case for that.
 * 
 * @author tharter
 *
 */
public class InitializerFactoryTest {

	/**
	 * Test method for {@link com.giantelectronicbrain.catfood.initialization.InitializerFactory#getConfiguration()}.
	 */
	@Test
	public void testGetConfiguration() {
		InitializerFactory.setConfiguration(new Properties());
		IInitializer config = null;
		try {
			config = InitializerFactory.getInitializer();
		} catch (InitializationException e) {
			e.printStackTrace();
			fail("initialization failed");
		}
		assertNotNull("Config should be constructed",config);
		String actual = null;
		try {
			actual = (String) config.get(InitializerFactory.WEBROOT);
		} catch (InitializationException e) {
			fail("Initialized object could not be recovered");
		}
		assertEquals("Parameter should be initialized","webroot", actual);
	}

}
