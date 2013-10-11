package com.haiming.messageboard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	//字段的名称
	String value();
	
	//字段的类型
	Class<?> type() default String.class;
	//字段的长度
	int length() default 0;

}
