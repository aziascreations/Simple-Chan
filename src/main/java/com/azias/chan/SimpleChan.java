package com.azias.chan;

import com.sun.net.httpserver.HttpServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * @version 1.0.0
 */
public class SimpleChan {
	private static final int WEB_SERVER_PORT = 48080;
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleChan.class);
	
	public static void main(String[] args) throws IOException {
		logger.debug("Preparing boards ArrayList...");
		ArrayList<Board> boards = new ArrayList<>();
		boards.add(new Board("a", "Anime & Manga"));
		boards.add(new Board("b", "Random"));
		boards.add(new Board("v", "Vydia"));
		
		logger.debug("Preparing SimpleHtmlCreator...");
		SimpleHtmlCreator shc = new SimpleHtmlCreator(boards, "/web/templates");
		
		logger.debug("Preparing HttpServer...");
		HttpServer server = HttpServer.create(new InetSocketAddress(WEB_SERVER_PORT), 0);
		server.createContext("/assets", new WebAssetHandler());
		
		for(Board board : boards) {
			if(!board.getId().matches("[a-z]+")) {
				throw new IOException("Board id "+board.getId()+" isn't valid !");
			}
			
			if(board.getId().matches("(assets|board|api)")) {
				throw new IOException("Board "+board.getName()+" uses a reserved id !");
			}
			
			server.createContext("/api/v1/"+board.getId(), new WebBoardApiHandler(board));
		}
		
		server.createContext("/board", new WebBoardPageHandler(shc, boards));
		server.createContext("/", new WebDefaultPageHandler(shc, boards));
		server.setExecutor(null);
		server.start();
		
		logger.info("Simple-Chan is now running on port {} !", WEB_SERVER_PORT);
	}
}
