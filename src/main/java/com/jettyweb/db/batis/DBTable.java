package com.jettyweb.db.batis;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DBTable {

	String value();

	boolean autoWire() default true;

	boolean cachable() default true;
}
