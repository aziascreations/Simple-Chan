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
 *
 * @version 1.0.0
 */
public class WebAssetHandler implements HttpHandler {
	public static final String RESOURCE_PREFIX_DEFAULT = "/web/assets/";
	
	private static final Logger logger = LoggerFactory.getLogger(WebAssetHandler.class);
	
	private String resourcePathPrefix;
	
	public WebAssetHandler() {
		this(RESOURCE_PREFIX_DEFAULT);
	}
	
	public WebAssetHandler(String resourcePathPrefix) {
		logger.trace("Instantiating {} with \"{}\" as the resourcePathPrefix...", this.getClass().getName(),
				resourcePathPrefix);
		this.resourcePathPrefix = resourcePathPrefix;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		logger.trace("{} is handling a {} request for {}", this.getClass().getName(), exchange.getRequestMethod(),
				exchange.getRequestURI().getPath());
		
		// Someone used a method that wasn't GET.
		if(!exchange.getRequestMethod().toUpperCase().equals("GET")) {
			logger.warn("Someone made a {} request int the {} context with the following URL: {}",
					exchange.getRequestMethod(), exchange.getHttpContext().getPath(),
					exchange.getRequestURI().getPath());
			exchange.sendResponseHeaders(405, 0);
			exchange.close();
			return;
		}
		
		// Trimming the website and context part from the URL and prepending the resourcePathPrefix variable.
		Path desiredResourcePath = Paths.get(resourcePathPrefix,
				exchange.getRequestURI().getPath()
						.replaceFirst("^" + exchange.getHttpContext().getPath(),
								""));
		
		logger.trace("Someone requested: {}", desiredResourcePath.toString());
		
		// Simple 404 error
		if(!ResourceHelpers.isResourceAvailable(desiredResourcePath)) {
			logger.trace("Someone requested a resource that doesn't exist: {} -> {}",
					exchange.getRequestURI().getPath(), desiredResourcePath.toString());
			exchange.sendResponseHeaders(404, 0);
			exchange.close();
			return;
		}
		
		InputStream is = ResourceHelpers.getResource(desiredResourcePath);
		
		// This a fail-safe check, it should never happen, but you never know.
		if(is == null) {
			logger.error("ResourceHelpers returned a null InputStream for: {}", desiredResourcePath.toString());
			exchange.sendResponseHeaders(500, 0);
			exchange.close();
			return;
		}
		
		byte[] response = is.readAllBytes();
		is.close();
		
		// Getting the file extension and setting the mime-type.
		int i = desiredResourcePath.getFileName().toString().lastIndexOf('.');
		if(i > 0) {
			exchange.getResponseHeaders().add("Content-Type", getMimeType(
					desiredResourcePath.getFileName().toString().substring(i+1).toLowerCase()
			));
		} else {
			exchange.getResponseHeaders().add("Content-Type", "text/plain");
		}
		
		logger.trace("Mime-Type for \"{}\" was set to: {}", desiredResourcePath.toString(),
				exchange.getResponseHeaders().get("Content-Type"));
		
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
