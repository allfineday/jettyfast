package com.jettyweb.redis;

import com.jettyweb.conf.AppInfo;
import com.jettyweb.exception.SystemException;
import com.jettyweb.listener.DBEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

public class BeanCache {

	protected Counter counter;
	private int ttlSec;

	private String pre;
	private String type;

	public BeanCache() {
		int c = AppInfo.getInt("sumk.cache.count", 500);
		this.counter = new Counter(c);
		ttlSec = AppInfo.getInt("sumk.cache.ttl", 3600 * 6);
	}

	public void setPre(String pre) {
		this.pre = pre;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCounter(Counter counter) {
		this.counter = counter;
	}

	public boolean accept(EventObject event) {
		return DBEvent.class.isInstance(event);
	}

	public Counter getCounter() {
		return counter;
	}

	public String get(String id) {
		if (counter.isCacheRefresh()) {
			return null;
		}
		String s = _get(id);
		if (s != null) {
			counter.incCached();
		}
		return s;
	}

	protected void onListen(EventObject event, Class<?> checkClass) {

	}

	protected String getKey(String id) {
		return pre + id;
	}

	protected String getType() {
		return type;
	}

	private String _get(String id) {
		Redis redis = RedisPool.readRedis(getType());
		return redis.get(getKey(id));
	}

	public void set(String id, String json) {
		String key = getKey(id);
		RedisPool.get(getType()).setex(key, ttlSec, json);
	}

	public void del(String id) {
		String key = getKey(id);
		RedisPool.get(getType()).del(key);
	}

	protected String[] getKeys(String[] ids) {
		String[] keys = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			keys[i] = getKey(ids[i]);
		}
		return keys;
	}

	public void delMulti(String[] ids) {
		if (ids == null || ids.length == 0) {
			return;
		}
		RedisPool.get(getType()).del(getKeys(ids));
	}

	public List<String> getMultiValue(Collection<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<String>();
		}
		String[] keys = getKeys(ids.toArray(new String[ids.size()]));
		return RedisPool.readRedis(getType()).mget(keys);
	}

	public List<String> getMultiValue(String[] ids) {
		if (ids == null || ids.length == 0) {
			return new ArrayList<String>();
		}
		String[] keys = getKeys(ids);
		return RedisPool.readRedis(getType()).mget(keys);
	}

	public void setMultiValue(String[] ids, final String[] values) {
		if (ids == null || ids.length == 0) {
			return;
		}
		if (ids.length != values.length) {
			SystemException.throwException(23432, "the length of ids is not equal to values");
		}
		String[] keys = getKeys(ids);
		Redis redis = RedisPool.get(getType());
		for (int i = 0; i < keys.length; i++) {
			redis.setex(keys[i], ttlSec, values[i]);
		}
	}

}
