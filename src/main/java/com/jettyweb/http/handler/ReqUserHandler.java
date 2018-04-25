package com.jettyweb.http.handler;

import com.jettyweb.exception.BizException;
import com.jettyweb.http.ErrorCode;
import com.jettyweb.http.Web;
import com.jettyweb.http.filter.Session;
import com.jettyweb.http.filter.UserSession;
import com.jettyweb.http.start.UserSessionHolder;
import org.eclipse.jetty.util.log.Log;

public class ReqUserHandler implements HttpHandler {

	@Override
	public boolean accept(Web web) {
		return web.requireLogin() || web.requestEncrypt().isAes();
	}

	@Override
	public boolean handle(WebContext ctx) throws Exception {
		String sessionID = ctx.getHeaders().get(Session.SESSIONID);
		
		Log.getLog().debug("01--------123456------sessionID:"+sessionID);
		
		UserSession session = UserSessionHolder.loadUserSession();
		
		Log.getLog().debug("03--------123456------session:"+session);
		
		byte[] key = session.getkey(sessionID);
		
		Log.getLog().debug("04--------123456------key:"+key);
		
		if (key == null) {
			BizException.throwException(ErrorCode.SESSION_ERROR, "请重新登陆");
		}
		ctx.setKey(key);
		session.flushSession();
		return false;
	}

}
