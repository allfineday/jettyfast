package com.jettyweb.bean;

import com.jettyweb.db.DBType;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Box {

	String dbName();

	DBType dbType() default DBType.ANY;

	/**
	 * 是否支持事务传递
	 */
	boolean embed() default true;
}
