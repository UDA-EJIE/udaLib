package com.ejie.x38.hdiv.controller.model;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LimitedCache<k,v> extends ConcurrentHashMap<k,v> {

	private static final long serialVersionUID = -7073421554369396756L;
	
	private static final int MAX_SIZE = 400;
	
	private static final int DELETION_BLOCK_SIZE = 20;
	
	private Queue<k> insertionQueue = new ConcurrentLinkedQueue<k>();
    

	@Override
	public v put(k key, v value) {
		int size = size();
		if(size > MAX_SIZE) { 
			int deleteSize =  size - MAX_SIZE + DELETION_BLOCK_SIZE;
			for(int i = 0; i< deleteSize; i++) {
				remove(insertionQueue.peek());
			}
		}
		return super.put(key, value);
	}
	
	@Override
	public void putAll(Map<? extends k, ? extends v> m) {
		for(Entry<? extends k, ? extends v> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public v remove(Object key) {
		insertionQueue.remove(key);
		return super.remove(key);
	}

	@Override
	public void clear() {
		insertionQueue.clear();
		super.clear();
	}
	
	

}
