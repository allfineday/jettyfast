package com.jettyweb.bean;

import java.lang.annotation.*;

/**
 * 表示是IOC中的bean。使用在类上面
 * 
 *
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
	String value() default "";

	Proxy proxy() default Proxy.CONFIG;
}
