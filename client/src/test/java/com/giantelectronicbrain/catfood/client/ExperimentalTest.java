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

import org.junit.Test;

import com.giantelectronicbrain.catfood.client.fluent.Att;
import com.giantelectronicbrain.catfood.client.fluent.Css;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;

/**
 * @author tharter
 *
 */
public class ExperimentalTest {

	@Test
	public void testCssAddRemove() {
		Fluent div = Fluent.body.div().css(Css.display, "none", Css.color, "red");
		String displayStyle = div.css(Css.display);
		String colorStyle = div.css(Css.color);
		assertEquals("none",displayStyle);
		assertEquals("red",colorStyle);
		div.css(Css.display,"block");
		displayStyle = div.css(Css.display);
		colorStyle = div.css(Css.color);
		assertEquals("block",displayStyle);
		assertEquals("red",colorStyle);
		div.css(Css.display,"none");
		displayStyle = div.css(Css.display);
		colorStyle = div.css(Css.color);
		assertEquals("none",displayStyle);
		assertEquals("red",colorStyle);
	}

	@Test
	public void testCssDelete() {
		Fluent div = Fluent.body.div().css(Css.display, "none", Css.color, "red");
		String displayStyle = div.css(Css.display);
		String colorStyle = div.css(Css.color);
		assertEquals("none",displayStyle);
		assertEquals("red",colorStyle);
		div.css(Css.display,null);
		displayStyle = div.css(Css.display);
		colorStyle = div.css(Css.color);
		assertEquals(null,displayStyle);
		assertEquals("red",colorStyle);
		div.css(Css.display,"none");
		displayStyle = div.css(Css.display);
		colorStyle = div.css(Css.color);
		assertEquals("none",displayStyle);
		assertEquals("red",colorStyle);
	}
	
	@Test
	public void testHide() {
		Fluent div = Fluent.body.div().css(Css.display, "block", Css.color, "red");
		String displayStyle = div.css(Css.display);
		String colorStyle = div.css(Css.color);
		assertEquals("block",displayStyle);
		assertEquals("red",colorStyle);
		div.hide(true);
		displayStyle = div.css(Css.display);
		colorStyle = div.css(Css.color);
		assertEquals("none",displayStyle);
		assertEquals("red",colorStyle);
		div.hide(false);
		displayStyle = div.css(Css.display);
		colorStyle = div.css(Css.color);
		assertEquals(null,displayStyle);
		assertEquals("red",colorStyle);
	}

}
