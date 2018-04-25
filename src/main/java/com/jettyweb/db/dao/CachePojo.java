package com.jettyweb.db.dao;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CachePojo {

	String type() default "";

	int seconds() default 0;

	String pre() default "";

	int interval() default 0;

	CacheType cacheType() default CacheType.SINGLE;
}
