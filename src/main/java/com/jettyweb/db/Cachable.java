package com.jettyweb.db;

public interface Cachable {
	String PRE = "cache.";

	boolean isCacheEnable();

	void setCacheEnable(boolean cache);

	String getModule();
}
