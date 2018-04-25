package com.jettyweb.http.handler;


import com.jettyweb.http.Web;

public class ToByteHandler implements HttpHandler {

	@Override
	public boolean accept(Web web) {
		return true;
	}

	@Override
	public boolean handle(WebContext ctx) throws Exception {
		String bs = (String) ctx.getResult();
		String charset = ctx.getCharset();
		ctx.setResult(bs.getBytes(charset));
		return false;
	}

}
