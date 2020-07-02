package com.azias.chan;

import java.util.LinkedList;

public class Board {
	private String id, name;
	private long currentBoardPostId;
	
	private LinkedList<Thread> threads;
	
	public Board(String id, String name) {
		this.id = id;
		this.name = name;
		
		currentBoardPostId = 0;
		threads = new LinkedList<>();
	}
	
	public boolean createThread(Post originalPost) {
		return createThread(originalPost, Thread.DEFAULT_TITLE);
	}
	
	public boolean createThread(Post originalPost, String title) {
		if(originalPost == null) {
			throw new NullPointerException("A null Post Object was added to a thread !");
		}
		
		return threads.add(new Thread(originalPost, title));
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasThreadById(long threadId) {
		if(threadId >= 0) {
			for(Thread thread : threads) {
				if(thread.getThreadId() == threadId) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Thread getThreadById(long threadId) {
		if(threadId >= 0) {
			for(Thread thread : threads) {
				if(thread.getThreadId() == threadId) {
					return thread;
				}
			}
		}
		
		return null;
	}
	
	public long getCurrentBoardPostId() {
		return currentBoardPostId;
	}
	
	public long getNextBoardPostId() {
		currentBoardPostId++;
		return currentBoardPostId;
	}
	
	public LinkedList<Thread> getThreads() {
		return threads;
	}
}
