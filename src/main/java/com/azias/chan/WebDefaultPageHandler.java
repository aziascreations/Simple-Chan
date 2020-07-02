package com.azias.chan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;

public class WebDefaultPageHandler implements HttpHandler {
	private final static Logger logger = LoggerFactory.getLogger(WebDefaultPageHandler.class);
	
	private SimpleHtmlCreator shc;
	private ArrayList<Board> boards;
	
	public WebDefaultPageHandler(SimpleHtmlCreator shc, ArrayList<Board> boards) throws IOException {
		if(boards == null) {
			throw new NullPointerException("A null Arraylist<Board> Object was given to WebDefaultHandler !");
		}
		if(shc == null) {
			throw new NullPointerException("A null SimpleHtmlCreator Object was given to WebDefaultHandler !");
		}
		
		this.shc = shc;
		this.boards = boards;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if(exchange.getRequestMethod().toUpperCase().equals("GET")) {
			if(exchange.getRequestURI().getPath().equals("/")) {
				String response = shc.getIndexPage(boards);
				exchange.sendResponseHeaders(200, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} else {
				logger.debug("Redirected user to root from: {}", exchange.getRequestURI().getPath());
				exchange.getResponseHeaders().add("Location", "/");
				exchange.sendResponseHeaders(301, 0);
				exchange.close();
			}
		} else {
			exchange.sendResponseHeaders(405, 0);
			exchange.close();
		}
	}
}
