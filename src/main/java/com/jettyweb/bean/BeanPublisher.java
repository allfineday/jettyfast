package com.jettyweb.bean;


import com.jettyweb.db.Cached;
import com.jettyweb.exception.SystemException;
import com.jettyweb.listener.Listener;
import com.jettyweb.listener.ListenerGroup;
import com.jettyweb.listener.ListenerGroupImpl;
import com.jettyweb.log.Log;
import com.jettyweb.util.ClassScaner;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * 监听器对外提供的接口
 *
 */
public final class BeanPublisher {

	private static ListenerGroup<BeanEvent> group = new ListenerGroupImpl<>();

	/**
	 * 发布扫描包的事件，包括添加到IOC，SOA接口或HTTP接口中
	 * 
	 * @param packageNames
	 */
	public static synchronized void publishBeans(String... packageNames) {
		ClassScaner scaner = new ClassScaner();
		Collection<String> clzs = scaner.parse(packageNames);
		for (String c : clzs) {
			try {
				publish(new BeanEvent(c));
			} catch (Exception e) {
				Log.printStack(e);
			}
		}
		Log.get(BeanPublisher.class).debug(IOC.info());
		autoWiredAll();
	}

	private static Object getInjectObject(Field f, Class<?> clz) {
		String name = f.getName();
		if (clz == Object.class) {
			clz = f.getType();
		}
		Object target = IOC.get(name, clz);
		if (target != null) {
			return target;
		}

		target = IOC.get(name, f.getType());
		if (target != null) {
			return target;
		}
		return IOC.get(clz);
	}

	private static Object getCacheObject(Field f) {
		String name = f.getName();
		Class<?> clz = f.getType();

		Object target = IOC.cache(name, f.getType());
		if (target != null) {
			return target;
		}
		return IOC.cache(null, clz);
	}

	private static void injectField(Field f, Object bean, Object target) {
		boolean access = f.isAccessible();
		if (!access) {
			f.setAccessible(true);
		}
		try {
			f.set(bean, target);
		} catch (Exception e) {
			Log.printStack(e);
		}
	}

	private static void autoWiredAll() {
		BeanPool pool = InnerIOC.pool;
		Collection<Object> beans = pool.allBeans();
		synchronized (pool) {

			beans.forEach(bean -> {
				Class<?> tempClz = bean.getClass();
				while (tempClz != null && (!tempClz.equals(Object.class))) {

					Field[] fs = tempClz.getDeclaredFields();
					for (Field f : fs) {
						Inject inj = f.getAnnotation(Inject.class);
						if (inj != null) {
							Class<?> clz = inj.beanClz();
							Object target = getInjectObject(f, clz);
							if (target == null) {
								SystemException.throwException(235435658,
										bean.getClass().getName() + "--" + f.getName() + " cannot injected");
							}
							injectField(f, bean, target);
							continue;
						}
						Cached c = f.getAnnotation(Cached.class);
						if (c != null) {
							Object target = getCacheObject(f);
							if (target == null) {
								SystemException.throwException(235435658,
										bean.getClass().getName() + "--" + f.getName() + " cannot injected");
							}
							injectField(f, bean, target);
							continue;
						}
					}
					tempClz = tempClz.getSuperclass();
				}
			});
		}
	}

	/**
	 * 发布事件
	 * 
	 * @param event
	 */
	public static void publish(BeanEvent event) {
		group.listen(event);
	}

	/**
	 * 添加监听器
	 * 
	 * @param listener
	 * @return
	 */
	public static synchronized boolean addListener(Listener<BeanEvent> listener) {
		group.addListener(listener);
		return true;
	}

	/**
	 * 移除监听器.如果监听器不存在，就返回null
	 * 
	 * @return
	 */
	public static synchronized void removeListener(Listener<BeanEvent> listener) {
		group.removeListener(listener);
	}

}