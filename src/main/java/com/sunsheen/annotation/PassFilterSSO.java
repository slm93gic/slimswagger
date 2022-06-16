package com.sunsheen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: FilterSso
 * @Description: (给接口是否开放权限 的注解   精细化控制 只能在方法上才可以)
 * @author: SLM
 * @date: 2020年3月18日 下午3:08:19
 *
 * @Copyright: 2020 www.sunsheen.cn Inc. All rights reserved.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PassFilterSSO {

}