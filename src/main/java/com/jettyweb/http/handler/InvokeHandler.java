package com.jettyweb.http.handler;


import com.jettyweb.exception.HttpException;
import com.jettyweb.http.HttpInfo;
import com.jettyweb.http.Web;

public class InvokeHandler implements HttpHandler {

	@Override
	public boolean accept(Web web) {
		return true;
	}

	@Override
	public boolean handle(WebContext ctx) throws Throwable {

		HttpInfo info = ctx.getInfo();
		if (!String.class.isInstance(ctx.getData())) {
			HttpException.throwException(this.getClass(), ctx.getData().getClass().getName() + " is not String");
		}
		Object obj = info.invokeByJsonArg((String) ctx.getData());
		ctx.setResult(obj);
		return false;
	}

}
