package com.jettyweb.http.filter;


import com.jettyweb.conf.AppInfo;
import com.jettyweb.http.HttpHeadersHolder;
import com.jettyweb.log.Log;
import com.jettyweb.main.TimedObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LocalUserSession implements UserSession {

	private Map<String, TimedObject> map = new ConcurrentHashMap<>();
	private Map<String, byte[]> keyMap = new ConcurrentHashMap<>();

	/**
	 * 
	 * @param sessionId
	 * @param key
	 * @return true表示保存成功，flase失败
	 */
	public void put(String sessionId, byte[] key) {
		keyMap.put(sessionId, key);
	}

	public LocalUserSession() {
		Log.get("session").info("use local user session");
		Thread t = new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						Set<String> set = map.keySet();
						long now = System.currentTimeMillis();
						for (String key : set) {
							TimedObject t = map.get(key);
							if (t == null) {
								continue;
							}
							if (now > t.getEvictTime()) {
								map.remove(key);
								keyMap.remove(key);
							}
						}
						Thread.sleep(TimeUnit.MINUTES.toMillis(1));
					} catch (Exception e) {
						Log.printStack(e);
					}
				}
			}

		};
		t.setDaemon(true);
		t.start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserObject(Class<T> clz) {
		TimedObject to = map.get(HttpHeadersHolder.token());
		if (to == null) {
			return null;
		}
		return (T) to.getTarget();
	}

	@Override
	public void flushSession() {
		TimedObject to = map.get(HttpHeadersHolder.token());
		if (to == null) {
			return;
		}
		to.setEvictTime(System.currentTimeMillis() + AppInfo.httpSessionTimeout * 1000);

	}

	@Override
	public void setSession(String key, Object sessionObj) {
		TimedObject to = new TimedObject();
		to.setTarget(sessionObj);
		to.setEvictTime(System.currentTimeMillis() + AppInfo.httpSessionTimeout * 1000);
		map.put(key, to);
	}

	@Override
	public void removeSession() {
		String token = HttpHeadersHolder.token();
		if (token == null) {
			return;
		}
		map.remove(token);
		keyMap.remove(token);
	}

	@Override
	public byte[] getkey(String sid) {
		return this.keyMap.get(sid);
	}

	@Override
	public void updateSession(Object sessionObj) {
		String token = HttpHeadersHolder.token();
		if (token == null) {
			return;
		}
		setSession(token, sessionObj);
	}

}
