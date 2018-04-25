package com.jettyweb.http.handler;

import com.jettyweb.http.Web;
import com.jettyweb.http.filter.Session;
import com.jettyweb.http.handler.HttpHandler;
import com.jettyweb.http.handler.WebContext;

import javax.servlet.http.HttpServletResponse;

/**
 * 用来写入内容主题，是最后一个handler
 * 
 * @author youtl
 *
 */
public class RespHeaderHandler implements HttpHandler {

	@Override
	public boolean accept(Web web) {
		return true;
	}

	@Override
	public boolean handle(WebContext ctx) throws Throwable {
		HttpServletResponse resp = ctx.getHttpResponse();
		resp.setCharacterEncoding(ctx.getCharset());
		String sessionID = ctx.getHeaders().get(Session.SESSIONID);
		if (sessionID != null && sessionID.length() > 0) {
			resp.setHeader(Session.SESSIONID, sessionID);
		}
		return false;
	}

}
