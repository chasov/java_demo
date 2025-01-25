package ru.t1.java.demo.metric.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Аннотация может применяться только к методам
@Retention(RetentionPolicy.RUNTIME)
public @interface Metric {
    long value() default 10;
}
