package com.jettyweb.http;


import com.jettyweb.http.filter.Session;

import javax.servlet.http.HttpServletRequest;

public class HttpHeadersHolder {
	private static ThreadLocal<HttpServletRequest> _req = new ThreadLocal<>();

	public static void setHttpRequest(HttpServletRequest req) {
		_req.set(req);
	}

	public static String getHeader(String name) {
		return _req.get().getHeader(name);
	}

	public static HttpServletRequest getHttpRequest() {
		return _req.get();
	}

	public static String token() {
		return _req.get().getHeader(Session.SESSIONID);
	}

}
