package com.jettyweb.asm;

import com.jettyweb.bean.Box;
import com.jettyweb.conf.AppInfo;
import com.jettyweb.exception.SystemException;
import com.jettyweb.log.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;


import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProxyClassFactory {

	public static Class<?> proxyIfNeed(Class<?> clz) throws Exception {
		if ("no".equals(AppInfo.get("sumk.aop.proxy", "config"))) {
			return clz;
		}
		Map<String, Method> aopMethods = new HashMap<>();
		Method[] bethods = clz.getDeclaredMethods();
		for (Method m : bethods) {
			if (!AsmUtils.canProxy(m.getModifiers())) {
				continue;
			}
			if (m.getAnnotation(Box.class) == null) {
				continue;
			}
			if (aopMethods.put(m.getName(), m) != null) {
				SystemException.throwException(-2321435, "the name of box method cannot duplicate in one class");
			}
		}
		if (aopMethods.isEmpty()) {
			return clz;
		}

		ClassReader cr = new ClassReader(AsmUtils.openStreamForClass(clz.getName()));

		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);

		String newClzName = AsmUtils.proxyCalssName(clz);
		ProxyClassVistor cv = new ProxyClassVistor(cw, newClzName, clz, aopMethods);
		cr.accept(cv, Vars.ASM_VER);

		byte[] code = cw.toByteArray();
		String clzOutPath = AppInfo.get("sumk.aop.debug.output");
		if (clzOutPath != null && clzOutPath.length() > 0) {
			try {
				File f = new File(clzOutPath, newClzName + ".class");
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(code);
				fos.close();
			} catch (Exception e) {
				if (Log.isTraceEnable("proxy")) {
					Log.printStack(e);
				}
			}
		}
		return AsmUtils.loadClass(newClzName, code);
	}
}
