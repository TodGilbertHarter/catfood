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
package com.giantelectronicbrain.catfood.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import com.giantelectronicbrain.catfood.IClient;
import com.giantelectronicbrain.catfood.client.Client;
import com.giantelectronicbrain.catfood.figwheely.FigWheelyServer;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

/**
 * @author tharter
 *
 */
public class GWTCompiler {
	private final static Logger log = LoggerFactory.getLogger(GWTCompiler.class);

//	private Class<?> classs;
	private String classs;
	private boolean debug;
	private boolean withHtml;

	/**
	 * Set the location of your source files if not /src, /src/main or
	 * /src/main/java .
	 */
	public static String folderSource = null;

	/**
	 * Set the charset of your application, if not utf-8; it will be put into the
	 * generated index.html.
	 */
	public static String charset = "utf-8";

	private static String folderBuild = null;

	/**
	 * Add GWT libraries to the path if you need to do so.
	 */
	public static List<String> librariesGwt;

	static {
		librariesGwt = new ArrayList<>();
//		librariesGwt.add("live.connector.vertxui.Vertxui");
		librariesGwt.add("com.github.nmorel.gwtjackson.GwtJackson");
		librariesGwt.add("com.google.gwt.logging.Logging");
		librariesGwt.add("ru.finam.slf4jgwt.logging.gwt.Logging");
		librariesGwt.add("ru.finam.slf4jgwt.api.API");
		librariesGwt.add("com.giantelectronicbrain.catfood.hairball");
		librariesGwt.add("com.giantelectronicbrain.catfood");
//		librariesGwt.add("commonmark");
	}

	private static boolean compiling = false;

	public static boolean isCompiling() {
		return compiling;
	}

	/**
	 * Get the target folder of the build. If overwritten, you get that one,
	 * otherwise you get one of the defaults: build/development or build/production
	 * depending on debug or not.
	 * 
	 * @param debugMode
	 *            whether we are in debug mode or not.
	 * @return the target folder of the java to javascript build
	 */
	public static String getTargetFolder(boolean debugMode) {
		if (folderBuild != null) {
			return folderBuild;
		}
		if (debugMode) {
			return "build/development";
		} else {
			return "build/production";
		}
	}

	/**
	 * Set the location of your target build folder, in case you do not want it in
	 * build/development (when debug=true) or build/production (when debug=false).
	 * 
	 * @param targetFolder
	 *            the target folder
	 */
	public static void setTargetFolder(String targetFolder) {
		folderBuild = targetFolder;
	}

//	private GWTCompiler(Class<?> classs, boolean debug, boolean withHtml) {
	private GWTCompiler(String classs, boolean debug, boolean withHtml) {
		this.classs = classs;
		this.debug = debug;
		this.withHtml = withHtml;

		// do a first translation
		if (Vertx.currentContext() != null) { // in a regular vertX run
			Vertx.currentContext().executeBlocking(future -> {
				if (translate() == false) {
					System.exit(0); // stop on startup errors
				}
				future.complete();
			}, result -> {
			});
		} else { // only translating in a test situation
			if (translate() == false) {
				System.exit(0); // stop on startup errors
			}
		}
	}

	/**
	 * Create a VertXUI static-handler at the target folder and translate the given
	 * class from java to javascript. Give url:null for only translating.
	 * 
	 * @param classs
	 *            the class that will be compiled to javascript
	 * @param urlWithoutAsterix
	 *            the url that will be served, but without asterix for the static
	 *            file handler; set to null if you only want compiling.
	 * @param debug
	 *            debug (true) or production (false)
	 * @param withHtml
	 *            with a generated .html file or not (advisable)
	 * @return the static file handler.
	 */
	public static Handler<RoutingContext> with(String iClient, String urlWithoutAsterix, boolean debug,
			boolean withHtml, Vertx vertx) {

//		Class<? extends IClient> classs = iClient.getClass();
		
		// Look for a sourcefolder. If none, we are in production so we don't do
		// anything at all.
//		String clientFile = classs.getName().replace(".", "/") + ".java";
		String clientFile = iClient.replace(".", "/") + ".java";
		Stream.of("src", "src/main", "src/main/java", "src/test", "src/test/java", folderSource).forEach(location -> {
			if (location != null && new File(location + "/" + clientFile).exists()) {
				folderSource = location;
			}
		});
		log.debug("source folder = {}",new File(folderSource).getAbsolutePath());
		if (folderSource == null) { // production
			if (debug) {
				throw new IllegalArgumentException(
						"Sourcefolder not found but debug is still true, you didn't set the 'working directory' of your "
								+ "IntelliJ-run to the root of the project? Or did you want to run with debug=false instead?");
			}
			log.info("Production mode: all OK, no source folder found, not translating from java to javascript.");
		} else { // inside IDE
			GWTCompiler translated = new GWTCompiler(iClient, debug, withHtml);

			if (FigWheelyServer.started) {
//				String clientFolder = (folderSource + "/" + classs.getName()).replace(".", "/");
				String clientFolder = (folderSource + "/" + iClient).replace(".", "/");
				clientFolder = clientFolder.substring(0, clientFolder.lastIndexOf("client") + 6);
				FigWheelyServer.addWatchable(urlWithoutAsterix + "a/a.nocache.js", clientFolder, translated);
			}
		}
		if (urlWithoutAsterix != null) { // only serve files when a target-URL is given
/*			return StaticHandler.create(GWTCompiler.getTargetFolder(debug)).setCachingEnabled(false)
					.setDefaultContentEncoding(GWTCompiler.charset); */
			return createTemplateHandler(vertx, iClient);
		} else {
			return null;
		}
	}

	private static Handler<RoutingContext> createTemplateHandler(Vertx vertx, String iClient) {
		ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);
		engine.setMode(TemplateMode.HTML);
		TemplateEngine tEngine = engine.getThymeleafTemplateEngine();
		FileTemplateResolver resolver = new FileTemplateResolver();
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setPrefix("webroot/");
		resolver.setSuffix(".html");
		tEngine.setTemplateResolver(resolver);
		TemplateHandler handler = TemplateHandler.create(engine);
		return (r) -> { 
			r.put("catfoodclient", new Client());
			handler.handle(r); 
		};
	}
	
	/**
	 * Start the translate. Continues asynchronously.
	 * 
	 * @return true when started successfully, false when an error occured.
	 */
	public boolean translate() {

		compiling = true;

		log.debug("Translating with targetfolder={}",new File(getTargetFolder(debug)).getAbsolutePath());
		log.debug("\tsourceFolder: {}",new File(folderSource).getAbsolutePath());
		log.debug("\tworking folder: {}",new File(".").getAbsolutePath());

		// Write index.html file which autoreloads
		File htmlFile = new File(getTargetFolder(debug) + "/index.html");
		if (withHtml) {
			try {
				log.debug("Writing temporary index.html to: {}",htmlFile.getAbsolutePath());
				FileUtils.writeStringToFile(htmlFile,
						"<!DOCTYPE html><html><head><meta http-equiv='refresh' content='1'/><style>"
								+ ".loader { border: 2px solid #f3f3f3; border-radius: 50%;"
								+ "border-top: 2px solid #3498db; width:30px; height:30px; -webkit-animation: spin 1.0s linear infinite;"
								+ "animation:spin 1.0s linear infinite; } "
								+ "@-webkit-keyframes spin { 0% { -webkit-transform: rotate(0deg);} 100% { -webkit-transform: rotate(360deg);}}"
								+ "@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg);}}"
								+ "</style></head><body><div class=loader></div></body></html>");
			} catch (IOException e) {
				log.error("Could not write index.html:{}",e.getMessage(), e);
				compiling = false;
				return false;
			}
		}

		// Write the .gml.xml file
		String className = classs; //.getName();
//		final String xmlFile = "gwtTemp";
		final String xmlFile = "a";
		// as path: the "client" package in the name of the classpath of the
		// given classname
		String path = className.replace(".", "/");
		path = path.substring(0, path.lastIndexOf("client") + 6);
		File gwtXml = new File(folderSource + "/" + xmlFile + ".gwt.xml");
		StringBuilder content = new StringBuilder("<module rename-to='a'>");
		librariesGwt.forEach(l -> content.append("<inherits name='" + l + "'/>"));
		content.append("<entry-point class='" + className + "'/><source path='" + path + "'/>");
		if (debug) {
			content.append("<set-property name='compiler.stackMode' value='emulated' />");
			content.append(
					"<set-configuration-property name='compiler.emulatedStack.recordLineNumbers' value='true' />");
			content.append("<set-property name=\"gwt.logging.logLevel\" value=\"ALL\"/>");          // default logLevel to log everything
			content.append("<set-property name=\"gwt.logging.enabled\" value=\"TRUE\"/>");            // To enable logging
			content.append("<set-property name=\"gwt.logging.consoleHandler\" value=\"ENABLED\"/>");	// Turn on console logging
			content.append("<set-configuration-property name='compiler.emulatedStack.recordFileNames' value='true'/>");
			content.append("<extend-configuration-property name=\"gwtjackson.configuration.extension\" "
					+ "value=\"com.giantelectronicbrain.catfood.client.JsonConfiguration\"/>");
		}
		content.append("</module>");
		try {
			log.debug("Writing gwt.xml to: {}",gwtXml.getAbsolutePath());
			FileUtils.writeStringToFile(gwtXml, content.toString());

			// sleep for slow systems
			for (int x = 0; x < 100; x++) {
				if (gwtXml.exists()) {
					break;
				}
				Thread.sleep(20);
			}
			if (!gwtXml.exists()) {
				throw new IOException("gwt-xml files does not exist although it was writte successfully");
			}
		} catch (IOException | InterruptedException e) {
			log.error("Could not write gwt xml file:{}",e.getMessage(), e);
			compiling = false;
			return false;
		}
		// Compile to javascript
		String options = "-strict -XdisableUpdateCheck -war " + new File(getTargetFolder(debug)).getAbsolutePath();
		if (debug) {
			options += " -draftCompile -optimize 0 -style DETAILED"; // -incremental
		} else {
			options += " -XnoclassMetadata -nodraftCompile -optimize 9 -noincremental";
		}

		// Extract and extend the classpath
		String separator = (System.getenv("path.separator") == null)
				? (System.getProperty("java.class.path").contains(";") ? ";" : ":")
				: System.getenv("path.separator");
		String classpath = System.getProperty("java.class.path");
		String lombokPath = new File(classpath).getAbsoluteFile().getParent() + "/lombok-1.18.20.jar";
		String ru_finamPath = new File(classpath).getAbsoluteFile().getParent() + "/slf4j-gwt-1.7.7.1.jar";
//		classpath = '"' + separator + classpath + separator + new File(folderSource).getAbsolutePath()  + separator + '"';
		classpath = '"' + separator + ru_finamPath + separator + lombokPath + separator + classpath + separator + new File(folderSource).getAbsolutePath()  + separator + '"';
		log.debug("Classpath = {}", classpath);

		// Check whether the classpath contains gwt
		/*
		 * if (!classpath.contains("gwt-dev")) { System.err.
		 * println("Error: classpath does not contain GWT for translating java to javascript."
		 * ); System.err.println("System property java.class.path is: " + classpath);
		 * compiling = false; return false; }
		 */
		// Run GWT
		try {
//			String commandline = "java -cp " + classpath + " com.google.gwt.dev.Compiler " + options + " " + xmlFile;
//			String commandline = "java -javaagent:/home/tharter/projects/catfood/gitvui/build/catfood/lib/lombok-1.18.20.jar=ECJ -cp " + classpath + " com.google.gwt.dev.Compiler " + options + " " + xmlFile;
			String commandline = "java -javaagent:"+lombokPath+"=ECJ -cp " + classpath + " com.google.gwt.dev.Compiler " + options + " " + xmlFile;
			log.debug("Starting GWT with commandline: {}",commandline);
			Process process = Runtime.getRuntime().exec(commandline);
			StringBuilder info = new StringBuilder();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader erput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			Context context = Vertx.currentContext();
			if (context == null) { // for TestDOM
				translateContinue(gwtXml, process, info, input, erput);
			} else {
				Vertx.currentContext().owner().setTimer(100,
						__ -> translateContinue(gwtXml, process, info, input, erput));
			}
		} catch (IOException e) {
			log.error("Could not start GWT compiler:{}" + e.getMessage(), e);
			compiling = false;
			return false;
		}
		return true;

		// OLD ATTEMPT TO RUN GWT EMBEDDED
		// OLD ATTEMPT TO RUN GWT EMBEDDED
		// OLD ATTEMPT TO RUN GWT EMBEDDED
		// List<File> list = new ArrayList<>();
		// list.add(new File("src"));
		// ModuleDef module = new ModuleDef(classs.getName());// ,
		// module.addResourcePath("src");
		// module.addEntryPointTypeName(classs.getName());
		// // module.addisInherited("elemental.Elemental");
		// // module.addPublicPackage("elemental.Elemental", new String[0], new
		// // String[0], new String[0], false, false);
		//
		// // Directly (works, but classpath does not contain 'src'
		// Memory.initialize();
		// SpeedTracerLogger.init();
		// CompilerOptions options = new CompilerOptionsImpl();
		// options.addModuleName(thisClass.getName());
		// options.setStrict(true);
		// options.setClassMetadataDisabled(true);
		// options.setDisableUpdateCheck(true);
		// if (debug) {
		// options.setIncrementalCompileEnabled(true);
		// } else {
		// options.setOptimizationLevel(CompilerOptions.OPTIMIZE_LEVEL_MAX);
		// options.setIncrementalCompileEnabled(false);
		// }
		// com.google.gwt.dev.Compiler.compile(new PrintWriterTreeLogger(),
		// options, module);
		// System.exit(0);
	}

	private void translateContinue(File gwtXml, Process process, StringBuilder info, BufferedReader input,
			BufferedReader erput) {
		// Read input
		try {
			while (input.ready()) {
				String line = input.readLine();
				log.trace("Gwt says: {}",line);
				info.append(line + "\n");
				if (line.contains("[ERROR]")) {
					System.err.print(".");
				} else {
					System.out.print(".");
				}
			}
		} catch (IOException e) {
			log.error("Could not read GWT input stream: {}" + e.getMessage(), e);
		}

		// Read error
		try {
			while (erput.ready()) {
				String line = erput.readLine();
				info.append("[ERROR]" + line + "\n");
				System.err.print(".");
			}
		} catch (IOException e) {
			log.error("Could not read GWT error stream: {}" + e.getMessage(), e);
		}

		// Break
		if (!process.isAlive()) {
//			gwtXml.delete();
			//TGH: using template engine now instead
			//writeHtml();
			String result = info.toString();
			if (result.contains("[ERROR]")) {
				System.err.println("Compile error(s): " + info);
			} else {
				System.out.println("*");
			}
			try {
				input.close();
			} catch (IOException ___) {
			}
			try {
				erput.close();
			} catch (IOException ___) {
			}
			compiling = false;
		} else { // continue

			Context context = Vertx.currentContext();
			if (context == null) { // for TestDOM
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				translateContinue(gwtXml, process, info, input, erput);
			} else {
				Vertx.currentContext().owner().setTimer(100,
						__ -> translateContinue(gwtXml, process, info, input, erput));
			}

		}
	}

	@SuppressWarnings("unchecked")
	private void writeHtml() {
		if (!withHtml) {
			return;
		}
		StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head><meta charset=\"" + charset + "\">");
		try {
			Method getScripts = Client.class.getDeclaredMethod("getScripts");
			for (String script : (ArrayList<String>) getScripts.invoke(null, (Object[]) null)) {
				html.append("<script src='");
				html.append(script);
				html.append("'></script>");
			}
		} catch (NoSuchMethodException e) {
			// is OK, does not exist
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			throw new IllegalArgumentException("Could not access public static ArrayList<String> getScripts()", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Could not access public static ArrayList<String> getScripts()", e);
		}
		try {
			Method getCss = Client.class.getDeclaredMethod("getCss");
			for (String css : (ArrayList<String>) getCss.invoke(null, (Object[]) null)) {
				html.append("<link rel=stylesheet href='");
				html.append(css);
				html.append("'/>");
			}
		} catch (NoSuchMethodException e) {
			// is OK, does not exist
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			throw new IllegalArgumentException("Could not access public static ArrayList<String> getCss()", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Could not access public static ArrayList<String> getCss()", e);
		}
		html.append("</head><body><script>");
		html.append("document.addEventListener('DOMContentLoaded', function(event) { ");
		html.append("var x = document.createElement('script'); ");
//		html.append("x.setAttribute('src','a/a.nocache.js?time=' + Math.random() ); ");
		html.append("x.setAttribute('src','/a/a.nocache.js?time=' + Math.random() ); ");
		html.append("document.body.appendChild(x); ");
		html.append("}); </script><div id='app-node'/></body></html>");

		// Write to file (not using vertx because this is also done with
		// non-vertx inside TestDOM)
		String htmlFile = GWTCompiler.getTargetFolder(debug) + "/index.html";
		log.debug("Writing final index.html to: {}",htmlFile);
		try (FileWriter fileWriter = new FileWriter(htmlFile)) {
			fileWriter.write(html.toString());
		} catch (IOException ie) {
			throw new IllegalArgumentException("Could not write index.html file", ie);
		}
	}
	
}
