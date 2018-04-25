package com.jettyweb.redis;

import redis.clients.jedis.Protocol;

public class RedisParamter {
	final static int DEFAULT_TRY_COUNT = 3;
	final static int DEFAULT_Timeout = 3000;

	public static RedisParamter create(String ip) {
		return new RedisParamter(ip);
	}

	public static RedisParamter create(String ip, int port) {
		RedisParamter p = new RedisParamter(ip);
		p.setPort(port);
		return p;
	}

	private int timeout = DEFAULT_Timeout;
	private String password = null;
	private String ip;
	private int db = 0;
	private int tryCount = DEFAULT_TRY_COUNT;
	private int port = Protocol.DEFAULT_PORT;

	public RedisParamter(String ip) {
		super();
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public RedisParamter setPort(int port) {
		this.port = port;
		return this;
	}

	public int getTimeout() {
		return timeout;
	}

	public RedisParamter setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public RedisParamter setPassword(String password) {
		this.password = password;
		return this;
	}

	public int getDb() {
		return db;
	}

	public RedisParamter setDb(int db) {
		this.db = db;
		return this;
	}

	public int getTryCount() {
		return tryCount;
	}

	public RedisParamter setTryCount(int tryCount) {
		this.tryCount = tryCount;
		return this;
	}

}
