package com.azias.chan;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleChan {
	private static final int DEFAULT_WEB_SERVER_PORT = 8080;
	
	public static void main(String[] args) throws IOException {
		ArrayList<Board> boards = new ArrayList<>();
		int serverPort = DEFAULT_WEB_SERVER_PORT;
		
		// Checking if "--help was used"
		if(Arrays.asList(args).contains("--help")) {
			System.out.println("Simple Chan - Help text");
			System.out.println("  --board=<board_id;board_name>  | Defines a new custom board.");
			System.out.println("  --help                         | Shows this help text.");
			System.out.println("  --port=<port>                  | Defines the port to be used.");
			System.out.println("Default values:");
			System.out.println("  port: 8080");
			System.out.println("  boards:");
			System.out.println("    * a - Anime & Manga");
			System.out.println("    * b - Random");
			System.out.println("    * v - Vydia");
			System.exit(0);
		}
		
		System.out.println("Starting SimpleChan...");
		
		// Parsing args...
		for(String arg : args) {
			if(arg.startsWith("--board=")) {
				String[] argData = arg.split("=")[1].split(";");
				System.out.println("Adding board '"+argData[0]+"' as: "+argData[1]);
				boards.add(new Board(argData[0], argData[1]));
			} else if(arg.startsWith("--port=")) {
				serverPort = Integer.decode(arg.split("=")[1]);
				System.out.println("Setting port to: "+serverPort);
			} else {
				// --help should be printed here since it exits before if given.
				System.err.println("Found unexpected argument: "+arg);
			}
		}
		
		// Checking if boards were given as arguments...
		if(boards.size() == 0) {
			System.out.println("Adding default boards...");
			boards.add(new Board("a", "Anime & Manga"));
			boards.add(new Board("b", "Random"));
			boards.add(new Board("v", "Vydia"));
		}
		
		System.out.println("Instantiating SimpleHtmlCreator...");
		SimpleHtmlCreator shc = new SimpleHtmlCreator(boards, "/web/templates");
		
		System.out.println("Preparing HttpServer...");
		HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
		server.createContext("/assets", new WebAssetHandler());
		
		for(Board board : boards) {
			if(!board.getId().matches("[a-z]+")) {
				throw new IOException("Board id "+board.getId()+" isn't valid !");
			}
			
			if(board.getId().matches("(assets|board|api)")) {
				throw new IOException("Board "+board.getName()+" uses a reserved API id !");
			}
			
			server.createContext("/api/v1/"+board.getId(), new WebBoardApiHandler(board));
		}
		
		server.createContext("/board", new WebBoardPageHandler(shc, boards));
		server.createContext("/", new WebDefaultPageHandler(shc, boards));
		server.setExecutor(null);
		server.start();
		
		System.out.println("Simple-Chan is now running on port "+serverPort+" !");
	}
}
