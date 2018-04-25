package com.jettyweb.redis;


import com.jettyweb.conf.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RedisPool {
	private static final Map<String, Redis> map = new ConcurrentHashMap<>();

	private static final Map<String, RedisParamter[]> readParamsMap = new ConcurrentHashMap<>();

	private static final Map<String, Redis[]> cacheReadMap = new ConcurrentHashMap<>();
	private static AtomicLong index = new AtomicLong();

	static Redis _defaultRedis;

	/**
	 * 获取已经存在的redis，获取不到就返回默认的，如果连默认的都没有，就返回null
	 * 
	 * @param alias
	 * @return
	 */
	public static Redis get(String alias) {
		Redis r = map.get(alias);
		if (r != null) {
			return r;
		}
		return _defaultRedis;
	}

	public static Redis readRedis(String alias) {
		if ("1".equals(AppInfo.get("sumk.redis.read.disable"))) {
			return get(alias);
		}
		Redis[] redisArray = cacheReadMap.get(alias);
		if (redisArray != null) {

			if (redisArray.length == 0) {
				return get(alias);
			}
			if (redisArray.length == 1) {
				return redisArray[0];
			}
			return redisArray[(int) (index.incrementAndGet() % redisArray.length)];
		}

		Redis redis = get(alias);
		RedisParamter[] params = readParamsMap.get(redis.getIp() + ":" + redis.getPort());
		if (params == null || params.length == 0) {
			cacheReadMap.put(alias, new Redis[0]);
			return redis;
		}
		synchronized (RedisPool.class) {
			List<Redis> redisList = new ArrayList<>();
			for (RedisParamter param : params) {
				Redis read = RedisFactory.get(null, param);
				redisList.add(read);
			}
			cacheReadMap.putIfAbsent(alias, redisList.toArray(new Redis[redisList.size()]));
			return redisList.get(0);
		}
	}

	public static Redis getRedisExactly(String alias) {
		return map.get(alias);
	}

	/**
	 * 默认的redis
	 * 
	 * @return
	 */
	public static Redis defaultRedis() {
		return _defaultRedis;
	}

	public static void put(String alias, Redis redis) {
		map.putIfAbsent(alias, redis);
	}

	static void attachRead(String host, RedisParamter[] reads) {
		readParamsMap.put(host, reads);
	}
}
