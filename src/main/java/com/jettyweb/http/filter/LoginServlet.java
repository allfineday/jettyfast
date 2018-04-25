package com.jettyweb.http.filter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LoginServlet {
	UserSession userSession();

	void init(ServletConfig config);

	void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

}
