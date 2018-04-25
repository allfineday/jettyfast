package com.jettyweb.http;

import java.lang.annotation.*;

/**
 * 声明在方法上，表示该方法可以被http调用。
 * 
 * 
 * @author youxia
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Upload {

	int maxSize() default 1024 * 1024 * 10;

	int maxFiles() default 1;

	/**
	 * 小写格式
	 * 
	 * @return
	 */
	String[] exts() default { ".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".gif", ".png", ".jpg", ".jpeg",
			".bmp" };

	Store tempStore() default Store.VM;
}
