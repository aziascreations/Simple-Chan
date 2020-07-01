package com.azias.chan;

import java.util.ArrayList;

public class Thread {
	public static final String DEFAULT_TITLE = "";
	public static final int SIZE_MAX_TITLE  = 128;
	
	private ArrayList<Post> posts;
	private long threadId;
	private boolean isSaged;
	
	public Thread(Post originalPost) {
		this(originalPost, originalPost.getPostId(), DEFAULT_TITLE);
	}
	
	public Thread(Post originalPost, String title) {
		this(originalPost, originalPost.getPostId(), title);
	}
	
	public Thread(Post originalPost, long threadId) {
		this(originalPost, threadId, DEFAULT_TITLE);
	}
	
	public Thread(Post originalPost, long threadId, String title) {
		if(originalPost == null) {
			throw new NullPointerException("Thread was initialized with a null Post Object !");
		}
		
		posts = new ArrayList<>();
		posts.add(originalPost);
		
		this.threadId = threadId;
		
		isSaged = false;
	}
	
	public ArrayList<Post> getPosts() {
		return posts;
	}
	
	public boolean addPost(Post originalPost) {
		if(originalPost == null) {
			throw new NullPointerException("A null Post Object was added to a thread !");
		}
		
		return posts.add(originalPost);
	}
	
	public long getThreadId() {
		return threadId;
	}
	
	// remove post
}
