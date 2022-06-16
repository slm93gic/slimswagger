package com.sunsheen;

import com.sunsheen.core.Propertiesgger;
import com.sunsheen.entity.SwaggerConfig;
import com.sunsheen.entity.SwaggerConfigBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class SlimggerRun {


    private Class clazz = null;

    private SlimggerRun() {

    }

    /**
     * 初始化参数
     *
     * @param clazz
     * @param ymlSource
     */
    public SlimggerRun(Class clazz) {
        this.clazz = clazz;
    }

    //启动
    public void start() {
        try {
            Constructor constructor = clazz.getConstructor();
            Propertiesgger propertiesSwagger = (Propertiesgger) constructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
