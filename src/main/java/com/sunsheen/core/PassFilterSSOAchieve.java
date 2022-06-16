package com.sunsheen.core;


import com.sunsheen.annotation.PassFilterSSO;
import com.sunsheen.utils.ClassAnnoScanerUtils;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: PassFilterSSOUtils
 * @Description: (去掉多余的监听)
 * @author: SLM
 * @date: 2020年5月8日 上午10:26:37
 * @Copyright: 2020 www.sunsheen.cn Inc. All rights reserved.
 */

@SuppressWarnings("all")
public class PassFilterSSOAchieve {

    private static String BASE_SCANN_PACK = "com.sunsheen";

    // 容器
    public static List<String> PassSSoPath = null;

    public static void start() {
        PassSSoPath = new ArrayList<String>();
        try {
            registeredAnnotation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: (获取注解信息)
     * @author: slm
     * @date: 2020年3月7日 下午6:11:05
     */
    private static void registeredAnnotation() throws Exception {
        List<Class> classsFromPackage = ClassAnnoScanerUtils.getClasssFromPackage(BASE_SCANN_PACK, Path.class);
        for (Class clazz : classsFromPackage) {
            for (Method method : clazz.getMethods()) {
                if (method.getAnnotation(PassFilterSSO.class) != null && method.getAnnotation(Path.class) != null) {
                    PassSSoPath.add(getPath(clazz, method)); // 获取key 添加到容器
                }

            }
        }
    }

    /**
     * @param clazz
     * @param method
     * @return
     */
    private static String getPath(Class clazz, Method method) {
        Path childAnno = (Path) method.getAnnotation(Path.class);
        Path paretAnno = (Path) clazz.getAnnotation(Path.class);
        String childPath = childAnno.value();
        if (null != paretAnno) {
            String parentPath = paretAnno.value();
            if (childPath.startsWith("/")) {
                childPath = parentPath + childPath;
            } else {
                childPath = parentPath + "/" + childPath;
            }
        }
        return childPath.replace("//", "/");
    }


}
