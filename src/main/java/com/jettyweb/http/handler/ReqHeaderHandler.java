package com.jettyweb.http.handler;


import com.jettyweb.http.HttpUtil;
import com.jettyweb.http.Web;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ReqHeaderHandler implements HttpHandler {

	@Override
	public boolean accept(Web web) {
		return true;
	}

	@Override
	public boolean handle(WebContext ctx) throws Exception {
		HttpServletRequest req = ctx.getHttpRequest();
		ctx.setCharset(HttpUtil.charset(req));
		ctx.setSign(req.getParameter("sign"));
		String data = req.getParameter("data");
		if (data != null) {
			ctx.setData(data);
		}
		Enumeration<String> names = req.getHeaderNames();
		Map<String, String> map = new HashMap<>();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			map.put(name, req.getHeader(name));
		}
		ctx.setHeaders(map);
		return false;
	}

}
