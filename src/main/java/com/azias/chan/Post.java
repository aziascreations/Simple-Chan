package com.azias.chan;

public class Post {
	public static final String DEFAULT_AUTHOR = "Anonymous";
	public static final int SIZE_MAX_AUTHOR = 64;
	
	public static final String DEFAULT_MESSAGE = "";
	public static final int SIZE_MAX_MESSAGE = 2048;
	
	private String author, message;
	private long postId, postDate;
	
	public Post(long postId) {
		this(postId, "", DEFAULT_AUTHOR, System.currentTimeMillis());
	}
	
	public Post(long postId, String message, String author, long postDate) {
		this.postId = postId;
		this.message = message;
		this.author = author;
		this.postDate = postDate;
	}
	
	public long getPostId() {
		return postId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getAuthor() {
		return author;
	}
}
