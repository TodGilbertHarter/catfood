package com.giantelectronicbrain.catfood.client;

public class FigWheelyClient {

	/**
	 * Convenient url for figwheely, change before using if you want to change
	 * it.
	 */
	public static String urlJavascript = "/figwheely.js";

	/**
	 * Send a string to the logging of the server. Note that you need to loead
	 * the figwheely script with head.scriptSync() in the client/browser
	 * sourcecode so that FigWheely.js is loaded synchronously. For example:
	 * 
	 * head.scriptSync(figwheelyLocation);<br>
	 * ......... <br>
	 * FigWheely.toServer("blabla");<br>
	 * 
	 * @param message
	 *            the message which will be distributed to all connected
	 *            browsers.
	 */
	public static final native void toServer(String message)/*-{
															if (window.top._fig) {
															 		if (window.top._fig.readyState === window.top._fig.OPEN){
																		window.top._fig.send("text: " + message);
																	} else {
																		window.top.console.log("FigWheely socket not ready yet: "+ window.top._fig.readyState);
																	}
																} else {
																	window.top.console.log("Could not be sended: " + message);
																}
															}-*/;

}
