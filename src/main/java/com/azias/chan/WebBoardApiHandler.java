package com.azias.chan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WebBoardApiHandler implements HttpHandler {
	private final static Logger logger = LoggerFactory.getLogger(WebBoardApiHandler.class);
	
	private Board board;
	
	public WebBoardApiHandler(Board board) {
		if(board == null) {
			throw new NullPointerException("A null Board Object was given to WebBoardHandler !");
		}
		
		this.board = board;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if(exchange.getRequestMethod().toUpperCase().equals("GET")) {
			// Redirecting to root if url points to "/board" instead of "/board/"
			if(exchange.getRequestURI()
					   .getPath()
					   .replaceFirst("^"+exchange.getHttpContext().getPath(), "").equals("")) {
				logger.debug("Redirected user to root of board.");
				exchange.getResponseHeaders().add("Location", "/"+board.getId()+"/");
				exchange.sendResponseHeaders(302, 0);
				exchange.close();
			}
			
			
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
			
			response += "</body></html>";
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else if(exchange.getRequestMethod().toUpperCase().equals("POST")) {
			HashMap<String, String> postFields;
			String requestBody, response = null;
			
			// Stripping the ...
			String requestedURL = exchange.getRequestURI()
										  .getPath()
										  .replaceFirst("^"+exchange.getHttpContext().getPath(), "")
										  .replaceFirst("^/", "")
										  .toLowerCase();
			
			logger.debug("Someone tried to POST on {}", requestedURL);
			
			switch(requestedURL) {
				case "thread":
				case "thread/":
					// The user is trying to create a thread.
					logger.debug("Someone is trying to make a thread");
					// Decoding is done later inside the parser.
					requestBody = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
					
					postFields = parsePostFields(requestBody, UTF_8);
					
					if(postFields != null) {
						// Verifying the fields
						String errorMessage = getPostFieldsErrorMessage(postFields, true);
						
						// Default return value, set to 500 as a fail-safe.
						int rCode = 500;
						
						if(errorMessage == null) {
							// Attempting to create the thread if possible.
							try{
								long newPostId = board.getNextBoardPostId();
								
								Post post = new Post(
										newPostId,
										postFields.get("message"),
										postFields.get("author"),
										System.currentTimeMillis()
								);
								
								board.createThread(post, postFields.get("title"));
								
								JSONObject json = new JSONObject();
								json.put("threadId", newPostId);
								response = json.toString();
								rCode = 200;
							} catch(Exception e) {
								logger.error("An error occurred while creating the Post !");
								e.printStackTrace();
								
								JSONObject json = new JSONObject();
								json.put("error", "Unknown error occurred !");
								response = json.toString();
								rCode = 500;
							}
						} else {
							JSONObject json = new JSONObject();
							json.put("error", errorMessage);
							response = json.toString();
							rCode = 400;
						}
						
						if(response != null) {
							exchange.sendResponseHeaders(rCode, response.length());
							OutputStream os = exchange.getResponseBody();
							os.write(response.getBytes());
							os.close();
						} else {
							exchange.sendResponseHeaders(rCode, 0);
							exchange.close();
						}
					}else {
						logger.warn("Failed to parse fields from \"{}\"", requestBody);
						exchange.sendResponseHeaders(400, 0);
						exchange.close();
					}
					
					break;
					
				case "post":
				case "post/":
					logger.debug("Someone is trying to make a post");
					
					// Decoding is done later inside the parser.
					requestBody = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
					
					postFields = parsePostFields(requestBody, UTF_8);
					
					if(postFields != null) {
						// Verifying the fields
						String errorMessage = getPostFieldsErrorMessage(postFields, false);
						
						// Default return value, set to 500 as a fail-safe.
						int rCode = 500;
						
						if(errorMessage == null) {
							// Attempting to verify if the thread exists and creating the post if possible.
							if(board.hasThreadById(Integer.parseInt(postFields.get("thread")))) {
								try {
									long newPostId = board.getNextBoardPostId();
									
									board.getThreadById(Integer.parseInt(postFields.get("thread"))).addPost(
											new Post(
													newPostId,
													postFields.get("message"),
													postFields.get("author"),
													System.currentTimeMillis()
											)
									);
									
									JSONObject json = new JSONObject();
									json.put("postId", newPostId);
									json.put("threadId", Integer.parseInt(postFields.get("thread")));
									response = json.toString();
									rCode = 200;
								} catch(Exception e) {
									logger.error("An error occurred while creating the Post !");
									e.printStackTrace();
									
									JSONObject json = new JSONObject();
									json.put("error", "Unknown error occurred !");
									response = json.toString();
									rCode = 500;
								}
							} else {
								JSONObject json = new JSONObject();
								json.put("error", "No thread with given id was found internally !");
								response = json.toString();
								rCode = 500;
							}
						} else {
							JSONObject json = new JSONObject();
							json.put("error", errorMessage);
							response = json.toString();
							rCode = 400;
						}
						
						if(response != null) {
							exchange.sendResponseHeaders(rCode, response.length());
							OutputStream os = exchange.getResponseBody();
							os.write(response.getBytes());
							os.close();
						} else {
							exchange.sendResponseHeaders(rCode, 0);
							exchange.close();
						}
					}else {
						logger.warn("Failed to parse fields from \"{}\"", requestBody);
						exchange.sendResponseHeaders(400, 0);
						exchange.close();
					}
					
					break;
					
				default:
					// The POST request was sent to the wrong "sub-context".
					logger.trace("Someone got a 404 while using POST in the {} context !",
							exchange.getHttpContext());
					exchange.sendResponseHeaders(404, 0);
					exchange.close();
					break;
			}
		} else {
			logger.warn("Someone used the {} method in the {} context !",
					exchange.getRequestMethod(), exchange.getHttpContext());
			exchange.sendResponseHeaders(405, 0);
			exchange.close();
		}
	}
	
	private HashMap<String, String> parsePostFields(String postData, Charset charset) {
		HashMap<String, String> postFields = null;
		
		if(postData.matches("^[\\d\\w\\W]+=[\\d\\w\\W]+(&[\\d\\w\\W]+=[\\d\\w\\W]+)*$")) {
			postFields = new HashMap<>();
			
			String[] dataFields = postData.split("&");
			
			for(String fieldPair : dataFields) {
				String[] fieldValues = fieldPair.split("=");
				
				if(fieldValues.length == 2) {
					postFields.put(
							URLDecoder.decode(fieldValues[0], charset),
							URLDecoder.decode(fieldValues[1], charset)
					);
				} else {
					logger.warn("Parsing failure !");
				}
			}
		} else {
			logger.warn("Parsing general failure !");
		}
		
		return postFields;
	}
	
	/**
	 * Verifies the post fields to make sure they are present and valid.
	 * Also attempts to fix inconsistencies like null fields.
	 * This is not an efficient way of doing his, but it works and I couldn't be bothered to play with Exceptions.
	 * @param fields
	 * @param isThread
	 * @return null if there is no error, otherwise it returns the reason an error occurred.
	 */
	private String getPostFieldsErrorMessage(HashMap<String, String> fields, boolean isThread) {
		if(isThread) {
			// thread specific fields
			if(fields.containsKey("title")) {
				if(fields.get("title").length() > Thread.SIZE_MAX_TITLE || fields.get("title").length() <= 0) {
					return "Invalid title size";
				}
			} else {
				fields.put("title", Thread.DEFAULT_TITLE);
			}
		} else {
			// post specific fields
			if(fields.containsKey("thread")) {
				try {
					Integer.parseInt(fields.get("thread"));
				} catch (NumberFormatException e) {
					return "Invalid thread id given !";
				}
			} else {
				return "No thread id given !";
			}
		}
		
		if(fields.containsKey("author")) {
			if(fields.get("author").length() > Post.SIZE_MAX_AUTHOR || fields.get("author").length() <= 0) {
				return "Invalid author size";
			}
		} else {
			fields.put("author", Post.DEFAULT_AUTHOR);
		}
		
		if(fields.containsKey("message")) {
			if(fields.get("message").length() > Post.SIZE_MAX_MESSAGE || fields.get("message").length() <= 0) {
				return "Invalid message size";
			}
		} else {
			if(isThread) {
				return "No thread message given !";
			} else {
				//fields.put("message", Post.DEFAULT_MESSAGE);
				return "No post message given !";
			}
		}
		
		return null;
	}
}
