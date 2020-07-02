package com.azias.chan;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class SimpleChan {
	private static final int WEB_SERVER_PORT = 80;
	
	private final static Logger logger = LoggerFactory.getLogger(SimpleChan.class);
	
	public static void main(String[] args) throws IOException {
		ArrayList<Board> boards = new ArrayList<>();
		boards.add(new Board("a", "Anime & Manga"));
		boards.add(new Board("b", "Random"));
		boards.add(new Board("v", "Vydia"));
		
		SimpleHtmlCreator shc = new SimpleHtmlCreator(boards, "/web/templates");
		
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
	}
}
