package org.jczh.appliedxml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElementMap {
	String prefix() default "";

	String name() default "";

	String entry() default "";

	String value() default "";

	String key() default "";

	boolean hideEntry() default false;

	boolean keyAsAttribute() default false;

	boolean valueAsText() default false;

	boolean required() default true;

}
