package com.sunsheen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: (多个参数说明)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParams {

    ApiParam[] value();

}
