package com.jettyweb.http.start;


import com.jettyweb.bean.AbstractBeanListener;
import com.jettyweb.bean.BeanEvent;
import com.jettyweb.bean.BeanPool;
import com.jettyweb.bean.InnerIOC;
import com.jettyweb.common.StartContext;
import com.jettyweb.http.Login;
import com.jettyweb.http.filter.LoginServlet;
import com.jettyweb.log.Log;

public class HttpBeanListener extends AbstractBeanListener {

	public HttpBeanListener(String packs) {
		super(packs);
	}

	private HttpFactory factory = new HttpFactory();

	@Override
	public void listen(BeanEvent event) {
		try {
			Class<?> clz = Class.forName(event.getClassName(), false, Thread.currentThread().getContextClassLoader());
			if (LoginServlet.class.isAssignableFrom(clz)) {
				Login login = clz.getAnnotation(Login.class);
				if (login != null) {
					InnerIOC.putClass(BeanPool.getBeanName(LoginServlet.class), clz);
					StartContext.inst.map.get().put(StartContext.HTTP_LOGIN_PATH, login.path());
				}
				return;
			}

			factory.resolve(clz);
		} catch (Throwable e) {
			Log.printStack(e);
		}
	}

}
