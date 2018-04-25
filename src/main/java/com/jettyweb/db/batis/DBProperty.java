package com.jettyweb.db.batis;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DBProperty {

	String value();
}
