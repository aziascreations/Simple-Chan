package com.azias.chan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * The WebDefaultPageHandler implements the HttpHandler interfaces and is used to serve the app's root or redirect
 *  users to it if they access an unhandled context.
 */
public class WebDefaultPageHandler implements HttpHandler {
	private final SimpleHtmlCreator shc;
	private final ArrayList<Board> boards;
	
	public WebDefaultPageHandler(SimpleHtmlCreator shc, ArrayList<Board> boards) {
		System.out.println("Instantiating " + this.getClass().getName() + "...");
		
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
		System.out.println(this.getClass().getName() + " is handling a " + exchange.getRequestMethod() +
								   "request for" + exchange.getRequestURI().getPath());
		
		// Checking if someone used a method that wasn't GET and sending 405.
		if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
			System.out.println("Someone made a " + exchange.getRequestMethod() +
									   " request in the " + exchange.getHttpContext().getPath() +
									   " context with the following URL: " + exchange.getRequestURI().getPath());
			exchange.sendResponseHeaders(405, 0);
			exchange.close();
			return;
		}
		
		// Redirecting the user to "/" with a 302 if they went to an unhandled context or invalid resource.
		if(!exchange.getRequestURI().getPath().equals("/")) {
			System.out.println("Redirected user to root from: " + exchange.getRequestURI().getPath());
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
