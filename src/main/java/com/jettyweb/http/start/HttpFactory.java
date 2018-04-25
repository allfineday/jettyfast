package com.jettyweb.http.start;



import com.jettyweb.asm.AsmUtils;
import com.jettyweb.bean.InnerIOC;
import com.jettyweb.common.MethodInfo;
import com.jettyweb.http.HttpHolder;
import com.jettyweb.http.HttpInfo;
import com.jettyweb.http.Upload;
import com.jettyweb.http.Web;
import com.jettyweb.log.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class HttpFactory {
	private HttpNameResolver nameResolver = new HttpNameResolver();

	public void resolve(Class<?> clz) throws Exception {
		Method[] methods = clz.getMethods();
		List<Method> httpMethods = new ArrayList<>();
		for (final Method m : methods) {
			if (AsmUtils.isFilted(m.getName())) {
				continue;
			}
			if (AsmUtils.notPublicOnly(m.getModifiers())) {
				continue;
			}
			if (m.getAnnotation(Web.class) != null) {
				httpMethods.add(m);
			}
		}
		if (httpMethods.isEmpty()) {
			return;
		}
		final Object obj = InnerIOC.putClass(null, clz);
		Class<?> proxyClz = obj.getClass();
		String classFullName = clz.getName();
		for (final Method m : httpMethods) {
			Web act = m.getAnnotation(Web.class);
			Upload upload = m.getAnnotation(Upload.class);
			String soaName = nameResolver.solve(clz, m, act.value());
			if (HttpHolder.getHttpInfo(soaName) != null) {
				Log.get("SYS.13").error(soaName + " already existed");
				continue;
			}
			Method proxyedMethod = AsmUtils.proxyMethod(m, proxyClz);
			int argSize = m.getParameterTypes().length;
			if (argSize == 0) {
				HttpHolder.putActInfo(soaName, new HttpInfo(obj, proxyedMethod, null, null, null, act, upload));
				continue;
			}
			MethodInfo mInfo = AsmUtils.createMethodInfo(classFullName, m);
			Class<?> argClz = AsmUtils.CreateArgPojo(classFullName, mInfo);
			HttpHolder.putActInfo(soaName,
					new HttpInfo(obj, proxyedMethod, argClz, mInfo.getArgNames(), m.getParameterTypes(), act, upload));
		}

	}

}
