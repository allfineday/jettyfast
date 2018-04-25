package com.jettyweb.bean;


import com.jettyweb.asm.AsmUtils;
import com.jettyweb.conf.AppInfo;
import com.jettyweb.db.Cachable;
import com.jettyweb.db.Cached;
import com.jettyweb.exception.SystemException;

/**
 * 用于创建Bean实例
 * 
 *
 *
 */
public class BeanFactoryListener extends AbstractBeanListener {

	public BeanFactoryListener(String packs) {
		super(packs);
	}

	@Override
	public void listen(BeanEvent event) {
		try {
			String clzName = event.getClassName();
			Class<?> clz = Class.forName(clzName, false, Thread.currentThread().getContextClassLoader());
			if (clz.isInterface() || clz.isAnnotation() || clz.isAnonymousClass()) {
				return;
			}
			if (AsmUtils.notPublicOnly(clz.getModifiers())) {
				return;
			}
			Bean b = clz.getAnnotation(Bean.class);
			if (b != null) {
				InnerIOC.putClass(b.value(), clz);
			}
			if ("true".equals(AppInfo.get("sumk.ioc.cache.disable", "false"))) {
				return;
			}
			Cached c = clz.getAnnotation(Cached.class);
			if (c != null) {
				Object bean = InnerIOC.putClass(Cachable.PRE + BeanPool.getBeanName(clz), clz);
				if (!Cachable.class.isInstance(bean)) {
					SystemException.throwException(35423543, clz.getName() + " is not instance of Cachable");
				}
				if ("cache".equals(AppInfo.get("sumk.dao.cache", "cache"))) {
					Cachable cache = (Cachable) bean;
					cache.setCacheEnable(true);
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			SystemException.throwException(-345365, "IOC error", e);
		}

	}

}
