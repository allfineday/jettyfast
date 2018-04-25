package com.jettyweb.http.filter;


import com.jettyweb.bean.IOC;
import com.jettyweb.log.Log;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpLoginWrapper extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private LoginServlet serv;

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serv.service(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			serv = IOC.get(LoginServlet.class);
			serv.init(config);
		} catch (Exception e) {
			Log.printStack(e);
		}
	}

}
