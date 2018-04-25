package com.jettyweb.redis;

import com.jettyweb.exception.SystemException;
import com.jettyweb.log.Log;
import com.jettyweb.util.Assert;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisFactory {

	private static final Map<String, Redis> map = new ConcurrentHashMap<String, Redis>();

	private static String toMapKey(String host, int port, int db) {
		return host + "_" + port + "#" + db;
	}

	public static Redis get(String ip) {
		RedisParamter p = RedisParamter.create(ip);
		return get(null, p);
	}

	/**
	 * 默认连接0库
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	public static Redis get(String ip, int port) {
		RedisParamter p = RedisParamter.create(ip, port);
		return get(null, p);
	}

	public static Redis get(String ip, int port, int timeout) {
		RedisParamter p = RedisParamter.create(ip, port);
		p.setTimeout(timeout);
		return get(null, p);
	}

	/**
	 * @param config
	 *            连接池配置. null的话就会采用默认设置
	 * @param p
	 *            自定义的连接参数.
	 * @return
	 */
	public static Redis get(JedisPoolConfig config, RedisParamter p) {
		Assert.notNull(p, "redis paramter cannot be null");
		if (p.getTryCount() < 1 || p.getTryCount() > 100) {
			throw new SystemException(54354354, "tryCount必须介于0和100之间");
		}
		String key = toMapKey(p.getIp(), p.getPort(), p.getDb());
		Redis redis = map.get(key);
		if (redis == null) {
			synchronized (RedisFactory.class) {
				if (map.containsKey(key)) {
					return map.get(key);
				}
				JedisPool pool = create(config, p.getIp(), p.getPort(), p.getTimeout(), p.getPassword(), p.getDb());
				redis = new Redis(pool, p);
				map.put(key, redis);
			}
		}
		return redis;
	}

	private static JedisPool create(JedisPoolConfig config, String host, int port, int timeout, String password,
			int database) {
		if (config == null) {
			config = defaultPoolConfig();
		}
		Log.get(RedisFactory.class, "create").info("create redis pool,host={},port={},db={}", host, port, database);
		return new JedisPool(config, host, port, timeout, password, database);

	}

	private static JedisPoolConfig defaultPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMinIdle(1);
		config.setMaxIdle(20);
		config.setMaxTotal(100);
		config.setTestWhileIdle(true);
		config.setTimeBetweenEvictionRunsMillis(5 * 60000);
		config.setNumTestsPerEvictionRun(3);
		return config;
	}

}
