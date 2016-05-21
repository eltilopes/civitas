package br.com.civitas.arquitetura.service;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(value = { java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
public @interface HandlerException {
	Class<? extends Exception> value() default Exception.class;
	String message() default "";
}
