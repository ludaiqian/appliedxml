package org.jczh.appliedxml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElementArray {

	String name() default "";

	String entry() default "";

	String prefix() default "";

	boolean required() default true;

}
