package com.jettyweb.http.handler;


import com.jettyweb.http.Web;

public interface HttpHandler {
	boolean accept(Web web);

	/**
	 *
	 * @return true表示处理完毕，false表示需要继续处理
	 */
	boolean handle(WebContext ctx) throws Throwable;
}
