package com.azias.chan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

// Was separated from ... to make it easier to read.

public class WebBoardPageHandler implements HttpHandler {
	private final static Logger logger = LoggerFactory.getLogger(WebBoardPageHandler.class);
	
	private SimpleHtmlCreator shc;
	private ArrayList<Board> boards;
	
	public WebBoardPageHandler(SimpleHtmlCreator shc, ArrayList<Board> boards) {
		this.shc = shc;
		this.boards = boards;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if(!exchange.getRequestMethod().toUpperCase().equals("GET")) {
			logger.warn("User has used an unsupported method in \"{}\": {}",
					exchange.getRequestURI().getPath(), exchange.getRequestMethod());
			exchange.sendResponseHeaders(405, 0);
			exchange.close();
			return;
		}
		
		// Grabbing the requested URL and removing the website address, context parts and leading slash.
		String requestedURL = exchange.getRequestURI().getPath()
									  .replaceFirst("^"+exchange.getHttpContext().getPath(),"")
									  .replaceFirst("^/", "");
		
		// Redirecting to the website/app root if URL is empty.
		if(requestedURL.length() <= 0) {
			logger.warn("Redirected user to root from: {}", exchange.getRequestURI().getPath());
			exchange.getResponseHeaders().add("Location", "/");
			exchange.sendResponseHeaders(301, 0);
			exchange.close();
			return;
		}
		
		Board requestedBoard = null;
		String[] requestedUrlParts = requestedURL.split("/");
		
		for(Board board : boards) {
			if(board.getId().equals(requestedUrlParts[0])) {
				requestedBoard = board;
				break;
			}
		}
		
		if(requestedBoard != null) {
			String response = "";
			
			if(requestedUrlParts.length >= 2) {
				// User is asking for a specific thread
				long desiredThreadId;
				
				try {
					desiredThreadId = Integer.parseInt(requestedUrlParts[1]);
				} catch (NumberFormatException e) {
					logger.warn("A user gave an invalid thread id : {}", requestedUrlParts[1]);
					exchange.sendResponseHeaders(400, 0);
					exchange.close();
					return;
				}
				
				Thread requestedThread = null;
				
				for(Thread thread : requestedBoard.getThreads()) {
					if(thread.getThreadId() == desiredThreadId) {
						requestedThread = thread;
						break;
					}
				}
				
				if(requestedThread != null) {
					response = shc.getThreadPage(requestedBoard, requestedThread);
				} else {
					logger.warn("User requested a thread that doesn't exist ({}), redirecting to root: {}",
							desiredThreadId,
							exchange.getRequestURI().getPath());
					exchange.getResponseHeaders().add("Location", "/");
					exchange.sendResponseHeaders(301, 0);
					exchange.close();
					return;
				}
				
			} else {
				// User is asking for the posts listing
				response = shc.getBoardPostsPageAsList(requestedBoard);
			}
			
			exchange.sendResponseHeaders(200, response.length());
			if(response.length() > 0) {
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} else {
				exchange.close();
			}
		} else {
			logger.warn("A user requested a board the doesn't exist: {}", requestedURL.split("/")[0]);
			exchange.sendResponseHeaders(404, 0);
			exchange.close();
		}
	}
}
