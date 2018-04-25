package com.jettyweb.common;

//import org.yx.db.DBSessionContext;

import java.lang.reflect.Method;

public abstract class BizExcutor {

	public static Object exec(Method m, Object obj, Object[] params) throws Exception {
		try {
			return m.invoke(obj, params);
		} catch (Exception e) {
			//DBSessionContext.clossLeakSession();
			throw e;
		}
	}

}
