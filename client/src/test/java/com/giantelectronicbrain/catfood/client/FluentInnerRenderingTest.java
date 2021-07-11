package com.giantelectronicbrain.catfood.client;

import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Li;
import static com.giantelectronicbrain.catfood.client.fluent.Fluent.Ul;

import org.junit.Ignore;
import org.junit.Test;

import com.giantelectronicbrain.catfood.client.fluent.Css;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;
import com.google.gwt.junit.client.GWTTestCase;

import elemental2.dom.Element;
import elemental2.dom.NodeList;

public class FluentInnerRenderingTest extends GWTTestCase {

//	@GwtIncompatible
	@Test
	public void testCssAdditionAndRemoval() {
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

	@Test
	public void testMiddleChildRemoval() {
		ViewOn<Integer> children = Fluent.body.add(0, i -> {
			switch (i) {
			case 0:
				return Ul(null, Li(null, "a"), Li(null, "b"), Li(null, "c"));
			case 1:
				return Ul(null, Li(null, "a"), Li(null, "b"), Li(null, "c"));
			case 2:
				return Ul(null, Li(null, "a"), Li(null, "c"));
			case 3:
				return Ul(null, Li(null, "a"), Li(null, "b"));
			case 4:
				return Ul(null, Li(null, "c"), Li(null, "b"), Li(null, "a"));
			default:
				return null;
			}
		});
		NodeList<Element> nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("1. length", 3, nodes.getLength());
		assertEquals("1. first value", "a", nodes.item(0).textContent);
		assertEquals("1. second value", "b", nodes.item(1).textContent);
		assertEquals("1. third value", "c", nodes.item(2).textContent);

		children.state(1);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("2. length", 3, nodes.getLength());
		assertEquals("2. first value", "a", nodes.item(0).textContent);
		assertEquals("2. second value", "b", nodes.item(1).textContent);
		assertEquals("2. third alue", "c", nodes.item(2).textContent);

		children.state(2);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("3. length", 2, nodes.getLength());
		assertEquals("3. first value", "a", nodes.item(0).textContent);
		assertEquals("3. second value", "c", nodes.item(1).textContent);

		children.state(3);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("4. length", 2, nodes.getLength());
		assertEquals("4. first value", "a", nodes.item(0).textContent);
		assertEquals("4. second value", "b", nodes.item(1).textContent);

		children.state(1);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("5. length", 3, nodes.getLength());
		assertEquals("5. first value", "a", nodes.item(0).textContent);
		assertEquals("5. second value", "b", nodes.item(1).textContent);
		assertEquals("5. third alue", "c", nodes.item(2).textContent);

		children.state(4);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("6. length", 3, nodes.getLength());
		assertEquals("6. first value", "c", nodes.item(0).textContent);
		assertEquals("6. second value", "b", nodes.item(1).textContent);
		assertEquals("6. third alue", "a", nodes.item(2).textContent);

	}

	@Test
	public void testInner() {
		String starttext = Math.random() + "aSeed";
		Fluent div = Fluent.body.div();

		assertEquals("0. real DOM is empty string before start", "", div.dom().textContent);

		div.txt(starttext);

		assertEquals("1. given value match virtual DOM", starttext, div.txt());
		assertEquals("1. given value match real DOM", starttext, div.dom().textContent);

		div.txt(starttext); // manualtest: should skip

		div.txt(null);
		assertEquals("2. given value match virtual DOM", null, div.txt());
		assertEquals("2. given value match real DOM not null but empty string", "", div.dom().textContent);

		div.txt(null); // manual test: should skip
	}

	public String getModuleName() {
		return "a";
	}

}
