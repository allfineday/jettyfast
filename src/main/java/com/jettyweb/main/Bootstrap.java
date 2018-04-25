package com.jettyweb.main;


import com.jettyweb.bean.BeanFactoryListener;
import com.jettyweb.bean.BeanPublisher;
import com.jettyweb.common.ServerStarter;
import com.jettyweb.common.StartContext;
import com.jettyweb.conf.AppInfo;
import com.jettyweb.http.start.HttpBeanListener;
import com.jettyweb.log.Log;
import com.jettyweb.redis.RedisLoader;
import com.jettyweb.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Bootstrap {
	public static void main(String[] args) {
		try {
			BeanPublisher.addListener(new BeanFactoryListener(AppInfo.get("ioc")));
			boolean  httpServer = false;

			String http = AppInfo.get("http");
			if (http != null && http.length() > 0) {
				BeanPublisher.addListener(new HttpBeanListener(http));
				httpServer = !Boolean.getBoolean("nohttp");
			}
			BeanPublisher.publishBeans(allPackage(http, AppInfo.get("ioc")));
			RedisLoader.init();

			if (httpServer) {
				int port = -1;
				try {
					port = Integer.valueOf(AppInfo.get("http.port", "80"));
				} catch (Exception e) {
					Log.get("SYS.45").error("http port {} is not a number");
				}
				if (port > 0) {
					String hs = AppInfo.get("http.starter.class", "com.jettyweb.http.start.HttpStarter");
					Class<?> httpClz = Class.forName(hs);
					ServerStarter httpStarter = (ServerStarter) httpClz.newInstance();
					httpStarter.start(port);
				}
			}
			StartContext.clear();
		} catch (Throwable e) {
			Log.printStack(e);
			System.exit(-1);
		}
	}

	/**
	 * 将逗号分隔符的字符串，拆分为无逗号的字符串
	 * 
	 * @param ps
	 *            每个字符串都可能含有,
	 * @return
	 */
	private static String[] allPackage(String... ps) {
		List<String> list = new ArrayList<String>();
		for (String p : ps) {
			if (StringUtils.isEmpty(p)) {
				continue;
			}
			p = p.replace('，', ',');
			String[] ss = p.split(",");
			for (String s : ss) {
				s = s.trim();
				if (s.isEmpty()) {
					continue;
				}
				list.add(s);
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
