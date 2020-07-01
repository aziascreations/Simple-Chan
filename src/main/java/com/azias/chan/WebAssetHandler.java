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
 * @author Herwin Bozet
 * @version 0.1
 */
public class WebAssetHandler implements HttpHandler {
	private final static Logger logger = LoggerFactory.getLogger(WebAssetHandler.class);
	
	private static final String RESOURCE_PREFIX = "/web/assets/";
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if(exchange.getRequestMethod().toUpperCase().equals("GET")) {
			Path desiredResourcePath = Paths.get(RESOURCE_PREFIX,
					exchange.getRequestURI().getPath()
							.replaceFirst("^" + exchange.getHttpContext().getPath(),
									""));
			
			logger.debug("Someone requested: {}", desiredResourcePath.toString());
			
			if(!ResourceHelpers.isResourceAvailable(desiredResourcePath)) {
				exchange.sendResponseHeaders(404, 0);
				exchange.close();
			} else {
				InputStream is = ResourceHelpers.getResource(desiredResourcePath);
				
				if(is == null) {
					exchange.sendResponseHeaders(500, 0);
					exchange.close();
				} else {
					byte[] response = is.readAllBytes();
					is.close();
					exchange.sendResponseHeaders(200, response.length);
					
					OutputStream os = exchange.getResponseBody();
					os.write(response);
					os.close();
				}
			}
		} else {
			exchange.sendResponseHeaders(405, 0);
			exchange.close();
		}
	}
}
