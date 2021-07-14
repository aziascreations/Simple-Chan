package com.azias.chan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * The WebDefaultPageHandler implements the HttpHandler interfaces and is used to serve the app's root or redirect
 *  users to it if they access an unhandled context.
 *
 * @version 1.0.0
 */
public class WebDefaultPageHandler implements HttpHandler {
	private static final Logger logger = LoggerFactory.getLogger(WebDefaultPageHandler.class);
	
	private SimpleHtmlCreator shc;
	private ArrayList<Board> boards;
	
	public WebDefaultPageHandler(SimpleHtmlCreator shc, ArrayList<Board> boards) throws IOException {
		logger.trace("Instantiating {}...", this.getClass().getName());
		
		if(shc == null) {
			throw new NullPointerException("A null SimpleHtmlCreator Object was given to "+this.getClass().getName()+" !");
		}
		if(boards == null) {
			throw new NullPointerException("A null Arraylist<Board> Object was given to "+this.getClass().getName()+" !");
		}
		
		this.shc = shc;
		this.boards = boards;
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
		
		// Redirecting the user to "/" with a 302.
		if(!exchange.getRequestURI().getPath().equals("/")) {
			logger.debug("Redirected user to root from: {}", exchange.getRequestURI().getPath());
			exchange.getResponseHeaders().add("Location", "/");
			exchange.sendResponseHeaders(302, 0);
			exchange.close();
		}
		
		// Serving the root page.
		String response = shc.getIndexPage(boards);
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
