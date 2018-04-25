package com.jettyweb.http;

import com.jettyweb.bean.Bean;

import java.lang.annotation.*;

/**
 * 声明在方法上，表示该方法可以被http调用。 如果对象的某个方法被Web注解，那么那个对象相当于被Bean注解
 * 
 * 
 * @author youxia
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Bean
public @interface Web {
	/**
	 * 服务名称，如果为空，就根据方法名获取
	 * 
	 * @return
	 */
	String value() default "";

	public String description() default "";

	public boolean requireLogin() default false;

	public EncryptType requestEncrypt() default EncryptType.NONE;

	public boolean sign() default false;

	public EncryptType responseEncrypt() default EncryptType.NONE;

}
