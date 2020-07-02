package com.azias.chan;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SimpleHtmlCreator {
	private static final String TEMPLATE_PATH_BASE = "base.html";
	private static final String TEMPLATE_PATH_INDEX = "index.html";
	private static final String TEMPLATE_PATH_BOARD_LIST = "board-list.html";
	private static final String TEMPLATE_PATH_BOARD_LIST_ROW = "board-list-row.html";
	private static final String TEMPLATE_PATH_THREAD_CONTAINER = "thread-container.html";
	private static final String TEMPLATE_PATH_THREAD_POST = "thread-post.html";
	
	private static final String TEMPLATE_FIELD_MAIN = "${sc.main}";
	private static final String TEMPLATE_FIELD_TITLE_PAGE = "${sc.title}";
	private static final String TEMPLATE_FIELD_TITLE_HEADER = "${sc.header.title}";
	
	private static final String TEMPLATE_FIELD_BOARD_LIST = "${sc.board.list}";
	
	private static final String TEMPLATE_FIELD_BOARD_POST_TITLE = "${sc.board.post.title}";
	private static final String TEMPLATE_FIELD_BOARD_POST_AUTHOR = "${sc.board.post.author}";
	private static final String TEMPLATE_FIELD_BOARD_POST_DATE = "${sc.board.post.date}";
	private static final String TEMPLATE_FIELD_BOARD_POST_ID = "${sc.board.post.id}";
	private static final String TEMPLATE_FIELD_BOARD_POST_MESSAGE = "${sc.board.post.message}";
	private static final String TEMPLATE_FIELD_BOARD_POST_GOTOURL = "${sc.board.post.goto.url}";
	
	private static final String TEMPLATE_FIELD_THREAD_POSTS = "${sc.thread.posts}";
	
	private static final String DATE_FORMAT = "d/M/y H:m:s";
	
	private String templateRootFolder;
	private String pageBase, pageIndex, pageBoardList, pageBoardListRow, pageThreadContainer, pageThreadPost;
	
	public SimpleHtmlCreator(String templateRootFolder) throws IOException {
		this.templateRootFolder = templateRootFolder;
		loadPageTemplates(templateRootFolder);
	}
	
	/* Can be used for reloads when using templates outside the jar */
	public void loadPageTemplates() throws IOException {
		loadPageTemplates(templateRootFolder);
	}
	
	public void loadPageTemplates(String templateRootFolder) throws IOException {
		pageBase = ResourceHelpers.getResourceAsString(Paths.get(templateRootFolder, TEMPLATE_PATH_BASE));
		pageIndex = ResourceHelpers.getResourceAsString(Paths.get(templateRootFolder, TEMPLATE_PATH_INDEX));
		pageBoardList = ResourceHelpers.getResourceAsString(Paths.get(templateRootFolder, TEMPLATE_PATH_BOARD_LIST));
		pageBoardListRow = ResourceHelpers.getResourceAsString(Paths.get(templateRootFolder, TEMPLATE_PATH_BOARD_LIST_ROW));
		pageThreadContainer = ResourceHelpers.getResourceAsString(Paths.get(templateRootFolder, TEMPLATE_PATH_THREAD_CONTAINER));
		pageThreadPost = ResourceHelpers.getResourceAsString(Paths.get(templateRootFolder, TEMPLATE_PATH_THREAD_POST));
	}
	
	public String getIndexPage(ArrayList<Board> boards) {
		String boardList = "";
		
		for(Board board : boards) {
			boardList += "<li><a href=\"/board/"+board.getId()+"/\">"+board.getName()+"</a></li>";
		}
		
		return pageBase.replace(TEMPLATE_FIELD_MAIN, boardList)
					   .replace(TEMPLATE_FIELD_TITLE_PAGE, "Simple Chan - Index")
					   .replace(TEMPLATE_FIELD_TITLE_HEADER, "Simple Chan - Index");
	}
	
	public String getBoardPostsPageAsList(Board board) {
		String postList = "";
		int i = 0;
		
		for(Thread thread : board.getThreads()) {
			postList += pageBoardListRow
								.replace(TEMPLATE_FIELD_BOARD_POST_TITLE, thread.getTitle())
								.replace(TEMPLATE_FIELD_BOARD_POST_AUTHOR, thread.getPosts().get(0).getAuthor())
								.replace(TEMPLATE_FIELD_BOARD_POST_DATE,
										new SimpleDateFormat(DATE_FORMAT).format(
												new Date(thread.getPosts().get(0).getPostDate())
										)
								)
								.replace(TEMPLATE_FIELD_BOARD_POST_ID, "#"+thread.getPosts().get(0).getPostId())
								.replace(TEMPLATE_FIELD_BOARD_POST_MESSAGE, thread.getPosts().get(0).getMessage())
								.replace(TEMPLATE_FIELD_BOARD_POST_GOTOURL, String.valueOf(thread.getPosts().get(0).getPostId()));
			if(i < board.getThreads().size() - 1) {
				postList += "<tr><td colspan=\"2\"><hr></td></tr>";
				i++;
			}
		}
		
		return pageBase.replace(TEMPLATE_FIELD_MAIN, pageBoardList)
					   .replace(TEMPLATE_FIELD_BOARD_LIST, postList)
					   .replace(TEMPLATE_FIELD_TITLE_PAGE, "Simple Chan - "+board.getName())
					   .replace(TEMPLATE_FIELD_TITLE_HEADER, board.getName());
	}
	
	public String getThreadPage(Board requestedBoard, Thread requestedThread) {
		String postList = "";
		int i = 0;
		
		for(Post post : requestedThread.getPosts()) {
			postList += pageThreadPost
								.replace(TEMPLATE_FIELD_BOARD_POST_AUTHOR, post.getAuthor())
								.replace(TEMPLATE_FIELD_BOARD_POST_DATE,
										new SimpleDateFormat(DATE_FORMAT).format(
												new Date(post.getPostDate())
										)
								)
								.replace(TEMPLATE_FIELD_BOARD_POST_ID, "#"+post.getPostId())
								.replace(TEMPLATE_FIELD_BOARD_POST_MESSAGE, post.getMessage());
			
			if(i < requestedThread.getPosts().size() - 1) {
				postList += "<hr>";
				i++;
			}
		}
		
		return pageBase.replace(TEMPLATE_FIELD_MAIN, pageThreadContainer)
					   .replace(TEMPLATE_FIELD_THREAD_POSTS, postList)
					   .replace(TEMPLATE_FIELD_TITLE_PAGE, "Simple Chan - "+requestedBoard.getName())
					   .replace(TEMPLATE_FIELD_TITLE_HEADER, requestedBoard.getName())
					   .replace(TEMPLATE_FIELD_BOARD_POST_TITLE, requestedThread.getTitle());
	}
}
