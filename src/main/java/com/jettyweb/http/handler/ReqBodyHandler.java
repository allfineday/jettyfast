package com.jettyweb.http.handler;

import com.jettyweb.exception.HttpException;
import com.jettyweb.http.HttpUtil;
import com.jettyweb.http.Web;
import com.jettyweb.log.Log;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

public class ReqBodyHandler implements HttpHandler {

	private final int MAXLENGTH = 1024 * 1024 * 10;

	@Override
	public boolean accept(Web web) {
		return true;
	}

	@Override
	public boolean handle(WebContext ctx) throws Exception {
		if (ctx.getData() != null) {
			Log.get(ReqBodyHandler.class).debug("data is not null");
			return false;
		}
		if (ctx.getInfo().getArgClz() == null) {
			return false;
		}
		HttpServletRequest req = ctx.getHttpRequest();
		InputStream in = req.getInputStream();
		int count = 0;
		int n = 0;
		byte[] temp = new byte[1024 * 4];
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while (-1 != (n = in.read(temp))) {
			output.write(temp, 0, n);
			count += n;
			if (count > MAXLENGTH) {
				HttpException.throwException(ReqBodyHandler.class, "request body is too long");
			}
		}
		byte[] bs = output.toByteArray();
		ctx.setData(HttpUtil.extractData(bs));
		output.close();
		return false;
	}

}
