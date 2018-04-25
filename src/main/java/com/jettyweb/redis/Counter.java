package com.jettyweb.redis;

public class Counter {

	private volatile long visitCount = 0;

	private volatile long cacheCount = 0;

	private int interval;

	public Counter(int interval) {
		super();
		this.interval = interval;
	}

	public long getVisitCount() {
		return visitCount;
	}

	public long getCachedCount() {
		return cacheCount;
	}

	public boolean isCacheRefresh() {
		return ++visitCount == interval;
	}

	public void incCached() {
		cacheCount++;
	}

	@Override
	public String toString() {
		return "visitCount=" + visitCount + ", cacheCount=" + cacheCount;
	}

}
