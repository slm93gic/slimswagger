package com.sunsheen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @Description: (单个参数说明)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParam {

    String name(); //字段名

    String note(); //字段说明

    String value() default ""; //参数

    Class<?> type() default String.class;   //参数类型

    boolean required() default false;  //是否必传
}
