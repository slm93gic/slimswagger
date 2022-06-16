package com.sunsheen.utils;


import java.util.Map;

/**
 * @ClassName: StringUtil
 * @Description: (字符工具)
 * @author: SLM
 * @date: 2019年12月12日 下午1:00:38
 * @Copyright: 2019 www.sunsheen.cn Inc. All rights reserved.
 */
@SuppressWarnings("all")
public class StringUtil {

    /**
     * @throws
     * @Description: (转换为字符串)
     * @param: @param obj
     * @param: @return
     * @return: String
     * @author: SLM
     * @date: 2019年12月12日 下午1:00:51
     */
    static String toString(Object obj) {
        return isEmpty(obj) ? null : "" + obj;
    }

    /**
     * @throws
     * @Description: (判断是否为空)
     * @param: @param obj
     * @param: @return
     * @return: boolean
     * @author: SLM
     * @date: 2019年12月12日 下午1:01:01
     */
    public static boolean isEmpty(Object obj) {
        return (obj == null || "".equals(obj) || "null".equals(obj) || "undefined".equals(obj) || ((obj + "").length() == 0));
    }

    /**
     * @throws
     * @Description: (将数据转换为小写)
     * @param: @param obj
     * @param: @return
     * @return: String
     * @author: SLM
     * @date: 2019年12月12日 下午1:01:12
     */
    static String toLowerCase(Object obj) {
        String word = toString(obj);
        return word == null ? null : word.toLowerCase();
    }

    /**
     * @throws
     * @Description: (map获取字符类型的数值)
     * @param: @param map
     * @param: @param key
     * @param: @return
     * @return: String
     * @author: SLM
     * @date: 2019年12月12日 下午1:01:26
     */
    public static String toString(Map<String, Object> map, String key) {
        return StringUtil.toString(map.get(key));
    }


}
