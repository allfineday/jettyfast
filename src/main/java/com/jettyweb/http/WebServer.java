package com.jettyweb.http;


import com.jettyweb.http.handler.HttpHandlerChain;
import com.jettyweb.http.handler.WebContext;
import com.jettyweb.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Administrator
 */
public class WebServer extends AbstractHttpServer {

	private static final long serialVersionUID = 74378082364534491L;

	@Override
	protected void handle(String act, HttpInfo info, HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		if (info.getUpload() != null) {
			Log.get(this.getClass()).error(act + " type error.It is not uploader");
			return;
		}
		
		if( "OPTIONS".equals(req.getMethod())) {
			resp.addHeader("Access-Control-Allow-Origin", "*");
			resp.addHeader("Access-Control-Allow-Headers", "sid");
			return;
		}
//		if (req.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(req.getMethod())) {
//			
			// CORS "pre-flight" request
			resp.addHeader("Access-Control-Allow-Origin", "*");
			resp.addHeader("Access-Control-Allow-Headers", "sid"); 
//		}

		WebContext wc = new WebContext(info, req, resp);
		HttpHandlerChain.inst.handle(wc);
	}
}
