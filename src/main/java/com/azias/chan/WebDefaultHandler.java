package com.azias.chan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class WebDefaultHandler implements HttpHandler {
	private final static Logger logger = LoggerFactory.getLogger(WebDefaultHandler.class);
	
	private ArrayList<Board> boards;
	
	public WebDefaultHandler(ArrayList<Board> boards) {
		if(boards == null) {
			throw new NullPointerException("A null Arraylist<Board> Object was given to WebDefaultHandler !");
		}
		
		this.boards = boards;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if(exchange.getRequestMethod().toUpperCase().equals("GET")) {
			if(exchange.getRequestURI().getPath().equals("/")) {
				
				String response = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">\n" +
										  "<title>${title}</title></head><body>\n" +
										  "\t\n";
				response += exchange.getRequestURI().getPath() + "<br>";
				response += exchange.getHttpContext().getPath() + "<br>";
				response += exchange.getRequestURI().getPath()
									.replaceFirst("^"+exchange.getHttpContext().getPath(),
											"") + "<br>";
				response += exchange.getRequestURI().getPath()
									.replaceFirst("^"+exchange.getHttpContext().getPath(),
											"")
									.replaceFirst("^/", "") + "<br>";
				response += "<br><br>";
				
				for(Board board : boards) {
					response += "<a href=\"/"+board.getId()+"/\">"+board.getName()+"</a><br>";
				}
				
				response += "</body></html>";
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
