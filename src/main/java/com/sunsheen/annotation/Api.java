package com.sunsheen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: (标识这个类是swagger的资源)
 * @author: SLM
 * @date: 2020年10月5日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {

    String tags();

    String descrition();

}
