package com.giantelectronicbrain.catfood.client.test;

import elemental2.dom.Console;
//import elemental2.dom.MemoryInfo;
//import elemental.util.Indexable;

/**
 * A class which simulates console.log for plain-java outside-gwt junit testing.
 * 
 * @author Niels Gorisse
 *
 */
public class ConsoleTester extends Console {

	public ConsoleTester() {
	}

//	@Override
	public Object getMemory() {
		return null;
	}

//	@Override
	public Object getProfiles() {
		return null;
	}

	@Override
	public void assert_(Object condition, Object... arg) {
		if (condition == null || true == (Boolean) condition) {
			System.err.println(arg);
		}
	}

	@Override
	public void count() {
	}

	@Override
	public void debug(Object... arg) {
		System.out.println(arg);
	}

	@Override
	public void dir(Object item) {
	}

	@Override
	public void dirxml(Object... item) {
	}

	@Override
	public void error(Object... arg) {
		System.err.println(arg);
	}

	@Override
	public void group(Object... arg) {
	}

	@Override
	public void groupCollapsed(Object... arg) {
	}

	@Override
	public void groupEnd() {
	}

	@Override
	public void info(Object... arg) {
		System.out.println(arg);
	}

	@Override
	public void log(Object... arg) {
		System.out.println(arg);
	}

//	@Override
	public void markTimeline() {
	}

//	@Override
	public void profile(String title) {
	}

//	@Override
	public void profileEnd(String title) {
	}

	@Override
	public void time(String title) {
	}

	@Override
	public void timeEnd(String title) {
	}

//	@Override
	public void timeStamp(Object arg) {
	}

	@Override
	public void trace(Object... arg) {
	}

	@Override
	public void warn(Object... arg) {
		System.err.println(arg);
	}

}
