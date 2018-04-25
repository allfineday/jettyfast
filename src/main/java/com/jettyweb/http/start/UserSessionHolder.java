package com.jettyweb.http.start;


import com.jettyweb.bean.IOC;
import com.jettyweb.exception.BizException;
import com.jettyweb.http.ErrorCode;
import com.jettyweb.http.filter.LoginServlet;
import com.jettyweb.http.filter.UserSession;
import com.jettyweb.log.Log;

public class UserSessionHolder {
	static UserSession session;

	public static UserSession userSession() {
		if (session == null) {
			LoginServlet serv = IOC.get(LoginServlet.class);
			session = serv.userSession();
		}
		return session;
	}

	/**
	 * 如果没有登陆，会抛出异常，而不是提示登录
	 * 
	 * @return
	 */
	public static UserSession loadUserSession() {
		if (session == null) {
			LoginServlet serv = IOC.get(LoginServlet.class);
			session = serv.userSession();
		}
		
		Log.get("http").debug("02--------123456------session:"+session);
		
		if (session == null) {
			BizException.throwException(ErrorCode.SESSION_ERROR, "请重新登陆.");
		}
		return session;
	}

	/**
	 * 获取session中的用户信息
	 * 
	 * @return
	 */
	public static Object getUserObject(Class<?> clz) {
		return loadUserSession().getUserObject(clz);
	}

	/**
	 * 移除session中的用户信息
	 */
	public static void remove() {
		userSession();
		if (session == null) {
			Log.get("session").debug("has removed");
			return;
		}
		session.removeSession();
	}

	/**
	 * 用新的对象更新session中的用户信息
	 * 
	 * @param sessionObj
	 */
	public static void updateUserObject(Object sessionObj) {
		loadUserSession().updateSession(sessionObj);
	}

}
