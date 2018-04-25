package com.jettyweb.redis;

import com.jettyweb.log.Log;
import com.jettyweb.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class RedisLoader {
	private static JedisPoolConfig defaultConfig = null;

	public static JedisPoolConfig getDefaultConfig() {
		return defaultConfig;
	}

	public static void setDefaultConfig(JedisPoolConfig defaultConfig) {
		RedisLoader.defaultConfig = defaultConfig;
	}

	private static final String REALONLE_PRE = "readonly.";

	public static void init() throws Exception {
		Properties p = new Properties();
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream("redis.properties");
			if (in == null) {
				return;
			}
			p.load(in);
			Log.get(RedisLoader.class).debug(p);
			Set<Object> keys = p.keySet();
			for (Object o : keys) {
				if (o == null || "".equals(o)) {
					continue;
				}
				String kk = (String) o;
				String v = p.getProperty(kk);
				v = v.trim();
				kk = kk.trim();
				if (kk.startsWith(REALONLE_PRE)) {
					createReadRedis(kk.substring(REALONLE_PRE.length()), v.split(","));
					continue;
				}
				Redis redis = create(v);
				String[] moduleKeys = kk.split(",");
				for (String key : moduleKeys) {
					if (StringUtils.isEmpty(key)) {
						continue;
					}
					if (StringUtils.isEmpty(v)) {
						continue;
					}
					if (RedisConstants.DEFAULT.equals(key)) {
						RedisPool._defaultRedis = redis;
					} else {
						RedisPool.put(key, redis);
					}
				}
			}
		} catch (Exception e) {
			Log.get(RedisLoader.class).error("failed to load redis pool from redis.properties");
			throw e;
		}
	}

	private static void createReadRedis(String host, String[] redisParams) throws Exception {
		if (StringUtils.isEmpty(host) || redisParams.length == 0 || !host.contains(":")) {
			return;
		}
		List<RedisParamter> list = new ArrayList<>();
		for (String param : redisParams) {
			param = param.trim();
			if (StringUtils.isEmpty(param)) {
				continue;
			}
			list.add(createParam(param));
		}
		if (list.isEmpty()) {
			return;
		}
		RedisPool.attachRead(host, list.toArray(new RedisParamter[list.size()]));
	}

	private static RedisParamter createParam(String v) throws Exception {
		String[] params = v.split("#");
		String ip = params[0];
		RedisParamter param;
		if (ip.contains(":")) {
			String[] addr = ip.split(":");
			ip = addr[0];
			param = RedisParamter.create(ip, Integer.parseInt(addr[1]));
		} else {
			param = RedisParamter.create(ip);
		}
		if (params.length > 1 && !StringUtils.isEmpty(params[1])) {
			param.setDb(Integer.parseInt(params[1]));
		}
		if (params.length > 2 && !StringUtils.isEmpty(params[2])) {
			param.setPassword(params[2]);
		}
		if (params.length > 3 && !StringUtils.isEmpty(params[3])) {
			param.setTimeout(Integer.parseInt(params[3]));
		}
		if (params.length > 4 && !StringUtils.isEmpty(params[4])) {
			param.setTryCount(Integer.parseInt(params[4]));
		}
		return param;
	}

	private static Redis create(String v) throws Exception {
		return RedisFactory.get(defaultConfig, createParam(v));
	}
}
