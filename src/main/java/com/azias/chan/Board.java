package com.azias.chan;

import java.util.LinkedList;

public class Board {
	private final String id, name;
	private final LinkedList<Thread> threads;
	
	private long currentBoardPostId;
	
	public Board(String id, String name) {
		this.id = id;
		this.name = name;
		
		currentBoardPostId = 0;
		threads = new LinkedList<>();
	}
	
	/**
	 * Create and registers a Thread Object with the given post and the default thread title.
	 * @param originalPost - A Post Object to be used as OP's post.
	 * @return A new Thread Object with the given post as OP's post and the default title.
	 */
	public boolean createThread(Post originalPost) {
		return createThread(originalPost, Thread.DEFAULT_TITLE);
	}
	
	/**
	 * Create and registers a Thread Object with the given post and title.
	 * @param originalPost - A Post Object to be used as OP's post.
	 * @param title - Title of the thread.
	 * @return A new Thread Object with the given post as OP's post.
	 */
	public boolean createThread(Post originalPost, String title) {
		if(originalPost == null) {
			throw new NullPointerException("A null Post Object was added to a thread !");
		}
		
		return threads.add(new Thread(originalPost, title));
	}
	
	/**
	 * Search if a Thread Object with a matching id exists in the current board.
	 * @param threadId The desired thread's id.
	 * @return true if the thread was found, false otherwise.
	 */
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
	
	/**
	 * Search and retrieve a Thread Object by its id in the current board.
	 * @param threadId The desired thread's id.
	 * @return The desired Thread Object or null.
	 */
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
	
	/**
	 * Increments and returns the board's latest post's id.
	 * @return The newly incremented board's latest post's id.
	 */
	public long getNextBoardPostId() {
		currentBoardPostId++;
		return currentBoardPostId;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public long getCurrentBoardPostId() {
		return currentBoardPostId;
	}
	
	public LinkedList<Thread> getThreads() {
		return threads;
	}
}
