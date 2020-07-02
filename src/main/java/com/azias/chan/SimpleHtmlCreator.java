package com.azias.chan;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final String TEMPLATE_FIELD_FOOTER = "${sc.footer}";
	
	private static final String TEMPLATE_FIELD_BOARD_LIST = "${sc.board.list}";
	
	private static final String TEMPLATE_FIELD_BOARD_POST_TITLE = "${sc.board.post.title}";
	private static final String TEMPLATE_FIELD_BOARD_POST_AUTHOR = "${sc.board.post.author}";
	private static final String TEMPLATE_FIELD_BOARD_POST_DATE = "${sc.board.post.date}";
	private static final String TEMPLATE_FIELD_BOARD_POST_ID = "${sc.board.post.id}";
	private static final String TEMPLATE_FIELD_BOARD_POST_MESSAGE = "${sc.board.post.message}";
	private static final String TEMPLATE_FIELD_BOARD_POST_GOTOURL = "${sc.board.post.goto.url}";
	
	private static final String TEMPLATE_FIELD_THREAD_POSTS = "${sc.thread.posts}";
	
	private static final String TEMPLATE_FIELD_FORM_THREADID = "${sc.form.threadid}";
	private static final String TEMPLATE_FIELD_FORM_BOARDID = "${sc.form.boardid}";
	
	private static final String DATE_FORMAT = "d/M/y H:m:s";
	
	private Pattern patternQuoteId = Pattern.compile("(?<quoteid>>>[0-9]+)");
	private Pattern patternQuote = Pattern.compile("(?<quote>>.+\\n)?");
	// I couldn't get this to work without this for some reason...
	private Pattern patternNewLine = Pattern.compile("(?<trash>\\n)");
	
	private ArrayList<Board> boards;
	private String templateRootFolder;
	private String pageBase, pageIndex, pageBoardList, pageBoardListRow, pageThreadContainer, pageThreadPost;
	
	public SimpleHtmlCreator(ArrayList<Board> boards, String templateRootFolder) throws IOException {
		this.boards = boards;
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
					   .replace(TEMPLATE_FIELD_TITLE_HEADER, "Simple Chan - Index")
					   .replace(TEMPLATE_FIELD_FOOTER, getGenericFooter());
	}
	
	public String getBoardPostsPageAsList(Board board) {
		String postList = "";
		int i = 0;
		
		if(board.getThreads().size() > 0) {
			for(Thread thread : board.getThreads()) {
				postList += pageBoardListRow
									.replace(TEMPLATE_FIELD_BOARD_POST_TITLE, thread.getTitle())
									.replace(TEMPLATE_FIELD_BOARD_POST_AUTHOR, thread.getPosts().get(0).getAuthor())
									.replace(TEMPLATE_FIELD_BOARD_POST_DATE,
											new SimpleDateFormat(DATE_FORMAT).format(
													new Date(thread.getPosts().get(0).getPostDate())
											)
									)
									.replace(TEMPLATE_FIELD_BOARD_POST_ID, String.valueOf(thread.getPosts().get(0).getPostId()))
									.replace(TEMPLATE_FIELD_BOARD_POST_MESSAGE, formatPostText(thread.getPosts().get(0).getMessage()))
									.replace(TEMPLATE_FIELD_BOARD_POST_GOTOURL, thread.getPosts().get(0).getPostId()+"/");
				if(i < board.getThreads().size() - 1) {
					postList += "<tr><td colspan=\"2\"><hr></td></tr>";
					i++;
				}
			}
		} else {
			postList = "<h2 id=\"board-list-empty\">There are no threads on this board !</h2>";
		}
		
		return pageBase.replace(TEMPLATE_FIELD_MAIN, pageBoardList)
					   .replace(TEMPLATE_FIELD_BOARD_LIST, postList)
					   .replace(TEMPLATE_FIELD_TITLE_PAGE, "Simple Chan - "+board.getName())
					   .replace(TEMPLATE_FIELD_TITLE_HEADER, board.getName())
					   .replace(TEMPLATE_FIELD_FOOTER, getGenericFooter() + " [ <a href=\"/\">home</a> ]")
					   .replace(TEMPLATE_FIELD_FORM_BOARDID, board.getId());
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
								.replace(TEMPLATE_FIELD_BOARD_POST_ID, String.valueOf(post.getPostId()))
								.replace(TEMPLATE_FIELD_BOARD_POST_MESSAGE, formatPostText(post.getMessage()));
			
			if(i < requestedThread.getPosts().size() - 1) {
				postList += "<hr>";
				i++;
			}
		}
		
		return pageBase.replace(TEMPLATE_FIELD_MAIN, pageThreadContainer)
					   .replace(TEMPLATE_FIELD_THREAD_POSTS, postList)
					   .replace(TEMPLATE_FIELD_TITLE_PAGE, "Simple Chan - "+requestedBoard.getName())
					   .replace(TEMPLATE_FIELD_TITLE_HEADER, requestedBoard.getName())
					   .replace(TEMPLATE_FIELD_BOARD_POST_TITLE, requestedThread.getTitle())
					   .replace(TEMPLATE_FIELD_FOOTER,
							   getGenericFooter() +
									   " [ <a href=\"/\">home</a> ]"+
									   " [ <a href=\"/board/"+requestedBoard.getId()+"/\">board</a> ]")
					   .replace(TEMPLATE_FIELD_FORM_BOARDID, requestedBoard.getId())
					   .replace(TEMPLATE_FIELD_FORM_THREADID, String.valueOf(requestedThread.getThreadId()));
	}
	
	private String getGenericFooter() {
		String footer = "[ ";
		int i = 0;
		
		for(Board board : boards) {
			footer += "<a href=\"/board/"+board.getId()+"/\">"+board.getId()+"</a>";
			if(i < boards.size() - 1) {
				footer += " | ";
				i++;
			}
		}
		
		return footer+" ]";
	}
	
	private String formatPostText(String postText) {
		//postText.replaceAll("[\\r\\n]+", "<br>");
		
		/*postText = patternQuoteId.matcher(postText)
						   .replaceAll("<span class=\"quote-id\">${quoteid}</span>");
		postText = patternQuote.matcher(postText)
						   .replaceAll("<span class=\"quote\">${quote}</span>");/**/
		postText = patternNewLine.matcher(postText)
						   .replaceAll("<br>");
		
		return postText;
	}
}
