package com.azias.chan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The WebAssetHandler implements the HttpHandler interfaces and is used to serve static content in a HttpServer context.
 */
public class WebAssetHandler implements HttpHandler {
	public static final String DEFAULT_RESOURCE_PREFIX = "/web/assets/";
	
	private static final Logger logger = LoggerFactory.getLogger(WebAssetHandler.class);
	
	/**
	 * Used as the root folder where the requested static resources are contained.
	 * The path goes like this: 'resourcePathPrefix' + URL (without the context).
	 * Be aware that no anti directory traversal check are done.
	 */
	private final String resourcePathPrefix;
	
	public WebAssetHandler() {
		this(DEFAULT_RESOURCE_PREFIX);
	}
	
	public WebAssetHandler(String resourcePathPrefix) {
		logger.trace("Instantiating {} with \"{}\" as the resourcePathPrefix...", this.getClass().getName(),
				resourcePathPrefix);
		this.resourcePathPrefix = resourcePathPrefix;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(this.getClass().getName() + " is handling a " + exchange.getRequestMethod() +
								   " request for " + exchange.getRequestURI().getPath());
		
		// Checking if someone used a method that wasn't GET and sending 405.
		if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
			System.err.println("Someone made a " + exchange.getRequestMethod() + " request in the " +
									   exchange.getHttpContext().getPath() + " context with the following URL: " +
									   exchange.getRequestURI().getPath());
			exchange.sendResponseHeaders(405, 0);
			exchange.close();
			return;
		}
		
		// Trimming the website and context part from the URL and prepending the resourcePathPrefix variable.
		Path desiredResourcePath = Paths.get(
				resourcePathPrefix,
				exchange.getRequestURI().getPath()
						.replaceFirst("^" + exchange.getHttpContext().getPath(),
								"")
		);
		System.out.println("Someone requested: " + desiredResourcePath);
		
		// Returning 404 is the resource could not be found.
		if(!ResourceHelpers.isResourceAvailable(desiredResourcePath)) {
			System.out.println("Someone requested a resource that doesn't exist: " +
									   exchange.getRequestURI().getPath() + " -> " + desiredResourcePath);
			exchange.sendResponseHeaders(404, 0);
			exchange.close();
			return;
		}
		
		// Preparing the InputStream and byte array for the resource that will be sent.
		InputStream is = ResourceHelpers.getResource(desiredResourcePath);
		if(is == null) {
			// This a fail-safe check, it should never happen, but you never know.
			System.err.println("ResourceHelpers returned a null InputStream for: " + desiredResourcePath);
			exchange.sendResponseHeaders(500, 0);
			exchange.close();
			return;
		}
		byte[] response = is.readAllBytes();
		is.close();
		
		// Getting the file extension and setting the mime-type appropriately.
		int i = desiredResourcePath.getFileName().toString().lastIndexOf('.');
		if(i > 0) {
			exchange.getResponseHeaders().add("Content-Type", getMimeType(
					desiredResourcePath.getFileName().toString().substring(i+1).toLowerCase()
			));
		} else {
			exchange.getResponseHeaders().add("Content-Type", "text/plain");
		}
		System.out.println("Mime-Type for \"" + desiredResourcePath + "\" was set to: " +
								   exchange.getResponseHeaders().get("Content-Type"));
		
		// Sending the response
		exchange.sendResponseHeaders(200, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response);
		os.close();
	}
	
	private String getMimeType(String fileExtension) {
		switch(fileExtension) {
			case "css":
				return "text/css";
			case "js":
				return "text/javascript";
			case "png":
				return "image/png";
			default:
				return "text/plain";
		}
	}
}
