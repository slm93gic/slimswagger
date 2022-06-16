package com.sunsheen.core;

import com.sunsheen.annotation.Bean;
import com.sunsheen.annotation.Config;
import com.sunsheen.entity.SwaggerConfig;
import com.sunsheen.utils.ClassAnnoScanerUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: (系统信息配置)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 **/
@SuppressWarnings("all")
public class Propertiesgger {

    private static Map<String, Object> slim = new HashMap<>();

    /**
     * @return
     */
    public static Map<String, Object> slimSwaggerPoperties() {
        if (null == slim) {
            new Propertiesgger();
        }
        return slim;
    }

    /**
     *
     */
    public Propertiesgger() {
        try {
            setPropertiesFromAnn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从注解中获取
     */
    private void setPropertiesFromAnn() {
        String classPath = "com.sunsheen";
        List<Class> list = ClassAnnoScanerUtils.getClasssFromPackage(classPath, Config.class);
        for (Class claszz : list) {
            Method[] methods = claszz.getMethods();
            for (Method method : methods) {
                Bean bean = method.getAnnotation(Bean.class);
                if (null != bean) {
                    putBaseInfomations(method, claszz);
                    return;
                }
            }
        }

    }

    /**
     * @param method
     * @param claszz
     */
    private void putBaseInfomations(Method method, Class claszz) {
        try {
            String name = method.getName();
            Object obj = method.invoke(claszz.newInstance());
            SwaggerConfig config = (SwaggerConfig) obj;
            slim.put("description", config.getDescription());
            slim.put("package", config.getScanner());
            slim.put("title", config.getTitle());
            slim.put("url", config.getUrl());
        } catch (Exception e) {

        }

    }


}

