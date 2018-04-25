package com.jettyweb.http;



import com.jettyweb.common.BizExcutor;
import com.jettyweb.exception.SystemException;
import com.jettyweb.util.GsonUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * http服务的信息
 *
 *
 */
public final class HttpInfo {

	private Method m;
	private String[] argNames;

	private Class<?>[] argTypes;
	private Field[] fields;
	private Web action;
	private Upload upload;
	private Object obj;

	private Class<?> argClz;

	public Field[] getFields() {
		return fields;
	}

	public Method getM() {
		return m;
	}

	public String[] getArgNames() {
		return argNames;
	}

	public Class<?>[] getArgTypes() {
		return argTypes;
	}

	public Web getAction() {
		return action;
	}

	public Object getObj() {
		return obj;
	}

	public Class<?> getArgClz() {
		return argClz;
	}

	public Upload getUpload() {
		return upload;
	}

	public HttpInfo(Object obj, Method m, Class<?> argClz, String[] argNames, Class<?>[] argTypes, Web action,
			Upload upload) {
		super();
		this.obj = obj;
		this.m = m;
		this.argClz = argClz;
		this.argNames = argNames;
		this.argTypes = argTypes;
		this.action = action;
		this.m.setAccessible(true);
		this.upload = upload;
		if (argClz != null) {
			this.fields = argClz.getFields();
			Arrays.stream(fields).forEachOrdered(f -> f.setAccessible(true));
		}
	}

	/**
	 * 使用参数执行方法，然后方法结果
	 * 
	 * @param args
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object invokeByJsonArg(String args) throws Exception {
		if (getArgClz() == null || argTypes == null || argTypes.length == 0) {
			return BizExcutor.exec(m, obj, null);
		}
		Object[] params = new Object[getArgTypes().length];
		Object argObj = GsonUtil.fromJson(args, argClz);
		for (int i = 0, k = 0; i < params.length; i++) {
			if (ActionContext.class.isInstance(getArgTypes()[i])) {

				params[i] = null;
				continue;
			}
			if (argObj == null) {
				params[i] = null;
				continue;
			}
			Field f = getFields()[k++];
			params[i] = f.get(argObj);
		}
		return BizExcutor.exec(m, obj, params);
	}

	/**
	 * 按参数顺序进行RPC调用
	 * 
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object invokeByOrder(String... args) throws Exception {
		if (argTypes == null || argTypes.length == 0) {
			return BizExcutor.exec(m, obj, null);
		}
		Object[] params = new Object[getArgTypes().length];
		if (getArgClz() == null) {

			return null;
		}
		if (args == null || args.length == 0) {
			SystemException.throwException(12012, m.getName() + "的参数不能为空");
		}
		if (args.length != argTypes.length) {
			SystemException.throwException(12013,
					m.getName() + "需要传递的参数有" + argTypes.length + "个，实际传递的是" + args.length + "个");
		}
		for (int i = 0, k = 0; i < params.length; i++) {
			if (ActionContext.class.isInstance(getArgTypes()[i])) {

				params[i] = null;
				continue;
			}
			if (args[k] == null) {
				params[i] = null;
				continue;
			}
			Field f = fields[k];
			params[i] = GsonUtil.fromJson(args[i], f.getGenericType());
			k++;
		}
		return BizExcutor.exec(m, obj, params);
	}

}
