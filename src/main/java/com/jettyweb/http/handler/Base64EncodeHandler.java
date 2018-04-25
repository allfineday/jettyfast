package com.jettyweb.http.handler;

import com.jettyweb.http.Web;
import org.apache.commons.codec.binary.Base64;
/**
 * base64解码
 * 
 * @author youtl
 *
 */
public class Base64EncodeHandler implements HttpHandler {

	@Override
	public boolean accept(Web web) {
		return web.responseEncrypt().isBase64();
	}

	@Override
	public boolean handle(WebContext ctx) throws Exception {
		byte[] bs = (byte[]) ctx.getResult();
		byte[] data = Base64.encodeBase64(bs);
		ctx.setResult(data);
		return false;
	}

}
