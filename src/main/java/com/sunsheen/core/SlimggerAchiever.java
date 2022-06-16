package com.sunsheen.core;

import com.sunsheen.annotation.Api;
import com.sunsheen.annotation.ApiOperation;
import com.sunsheen.annotation.ApiParam;
import com.sunsheen.annotation.ApiParams;
import com.sunsheen.utils.ClassAnnoScanerUtils;
import com.sunsheen.utils.StringUtils;

import javax.ws.rs.*;
import java.lang.reflect.Method;
import java.util.*;


/**
 * @Description: (接口的实现)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 **/
@SuppressWarnings("all")
public class SlimggerAchiever {

    private static List<Map<String, Object>> APIS = new ArrayList<>();
    private static HashSet<String> apiHashSet = new HashSet<>();
    private static boolean flag = false;


    /**
     * 获取接口数据
     *
     * @return
     */
    public static List<Map<String, Object>> getApis() {
        if (APIS.isEmpty()) {
            new SlimggerAchiever().init();
            sleep();
            //排序接口
            doSort(APIS);
        }
        return APIS;
    }

    /**
     * 获取每个数据在排序
     *
     * @param lists
     */
    public static void doSort(List<Map<String, Object>> lists) {
        for (Map<String, Object> map : lists) {
            List<Map<String, Object>> values = (List<Map<String, Object>>) map.get("values");
            sortArrays(values);
        }

    }


    /**
     * 排序算法
     *
     * @param lists
     */
    public static void sortArrays(List<Map<String, Object>> lists) {
        Collections.sort(lists, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                String value1 = (String) map1.get("path");
                String value2 = (String) map2.get("path");
                return value1.compareTo(value2);
            }
        });
    }

    /**
     * 时间静止
     */
    private static void sleep() {
        while (!flag) {
            return;
        }
    }

    /**
     * 重新扫描
     */
    public static void reScanner() {
        APIS = new ArrayList<>();
        apiHashSet = new HashSet<>();
        flag = false;
        getApis();
    }


    /**
     * 初始化配置
     */
    public void init() {
        //注解获取配置
        Map<String, Object> propertiesMap = new Propertiesgger().slimSwaggerPoperties();
        String scannpackage = StringUtils.toString(propertiesMap, "package");
        List<Class> classsFromPackage = ClassAnnoScanerUtils.getClasssFromPackage(scannpackage, Path.class);
        if (!classsFromPackage.isEmpty()) {
            registeredAnnotation(classsFromPackage);
        }
        flag = true;
    }


    /**
     * @Description: (获取注解信息)
     * @author: slm
     * @date: 2020年3月7日 下午6:11:05
     */
    private void registeredAnnotation(List<Class> classsFromPackage) {
        for (Class clazz : classsFromPackage) {
            //判断是否有@APi 则增加分类
            Api apiAnnotation = (Api) clazz.getAnnotation(Api.class);
            List<Map<String, Object>> apiContents = dealTags(apiAnnotation, clazz.getSimpleName()); //每个方法的容器
            dealMethPath(clazz, apiContents);  //处理每一个参数接口
        }
    }

    /**
     * tags层
     *
     * @param apiAnnotation
     * @return
     */
    private List<Map<String, Object>> dealTags(Api apiAnnotation, String className) {
        List<Map<String, Object>> apiContents = new ArrayList<>(); //每个方法的容器

        String tagsKey = "其他未标识的接口";
        String descrition = "未标识的接口";
        if (null != apiAnnotation) {
            tagsKey = apiAnnotation.tags();//获取tags分类
            descrition = apiAnnotation.descrition(); //tags的描述
        }

        //判单是否已经存在了tags
        for (Map<String, Object> map : APIS) {
            String tag = StringUtils.toString(map, "name");
            if (null != tag && tag.equals(tagsKey)) {
                apiContents = (List<Map<String, Object>>) map.get("values");
                break;
            }
        }
        if (apiContents.size() == 0) {
            Map<String, Object> tagMap = new HashMap<>();  //定义一个新的tag
            tagMap.put("descrition", descrition);
            tagMap.put("values", apiContents);
            tagMap.put("name", tagsKey);
            tagMap.put("className", className);
            APIS.add(tagMap);
        }
        return apiContents;
    }


    /**
     * 处理方法路径
     *
     * @param clazz
     * @param apiContents
     */
    private void dealMethPath(Class clazz, List<Map<String, Object>> apiContents) {
        String parentPath = parentPath(clazz);//获取类上的路径

        //遍历每个方法
        for (Method method : clazz.getMethods()) {
            Map<String, Object> apiMap = new HashMap<>();//包装到容器
            Path pathAnnotation = (Path) method.getAnnotation(Path.class);
            if (null != pathAnnotation) {
                if (methodPath(method, apiMap, parentPath)) { //该方法的路径
                    methDescrition(method, apiMap); //该方法的说明
                    methodParameters(method, apiMap);//该方法的参数
                    methodWay(method, apiMap);  //请求的方式
                    apiContents.add(apiMap); //添加到接口容器
                }
            }
        }
    }

    /**
     * 获取类上的路径
     *
     * @param clazz
     */
    private String parentPath(Class clazz) {
        return ((Path) clazz.getAnnotation(Path.class)).value();
    }


    /**
     * 方法路径
     *
     * @param method
     * @param api
     * @param parentPath
     */
    private boolean methodPath(Method method, Map<String, Object> api, String parentPath) {
        //方法上的路径
        String pathvalue = "";
        Path pathAnnotation = method.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            pathvalue = pathAnnotation.value();
        }

        //拼接全路径
        if (!parentPath.startsWith("/")) {
            parentPath = "/" + parentPath;
        }
        if (!pathvalue.startsWith("/")) {
            pathvalue = "/" + pathvalue;
        }
        pathvalue = parentPath + pathvalue;
        //是否已经存储
        int beforeSize = apiHashSet.size();
        apiHashSet.add(pathvalue);
        if (beforeSize != apiHashSet.size()) {
            api.put("path", pathvalue); //访问的路径
            return true;
        }
        return false;
    }


    /**
     * 方法的参数
     *
     * @param method
     * @param api
     */
    private void methodParameters(Method method, Map<String, Object> api) {
        List<Map<String, Object>> params = dealParams(method);  //获取参数
        if (params.size() == 0) {
            params = dealParamsWithAfter(method);
        }
        api.put("params", params); //所有参数
    }

    /**
     * 方法的请求方式
     *
     * @param method
     * @param api
     */
    private void methodWay(Method method, Map<String, Object> api) {
        String way = "";
        if (null != method.getAnnotation(POST.class)) {
            way += "POST,";
        }
        if (null != method.getAnnotation(DELETE.class)) {
            way += "DELETE,";
        }
        if (null != method.getAnnotation(GET.class)) {
            way += "GET,";
        }
        if (null != method.getAnnotation(OPTIONS.class)) {
            way += "OPTIONS,";
        }

        if (null != method.getAnnotation(PUT.class)) {
            way += "PUT";
        }
        if (!StringUtils.isEmpty(way)) {
            way = way.substring(0, way.lastIndexOf(","));
        }

        api.put("way", way); //所有参数
    }


    /**
     * 方法的说明
     *
     * @param method
     * @param api
     */
    private void methDescrition(Method method, Map<String, Object> api) {
        ApiOperation annotation = method.getAnnotation(ApiOperation.class);
        String methodNotes = (String) api.get("path");
        boolean explain = false;
        if (null != annotation) {
            methodNotes = annotation.notes(); //方法的说明
            explain = annotation.explain();
        }
        api.put("descrition", methodNotes);
        api.put("explain", explain);
    }


    /**
     * 获取单个参数
     *
     * @param apiImplicitParam
     * @return
     */
    private Map<String, Object> dealParam(ApiParam apiParam) {
        Map<String, Object> params = new HashMap<>();
        String name = "";
        String value = "";
        String note = "";
        boolean required = false;
        Class type = null;
        if (null != apiParam) {
            name = apiParam.name();
            value = apiParam.value();
            required = apiParam.required();
            type = apiParam.type();
            note = apiParam.note();
        }
        params.put("name", name);
        params.put("note", note);
        params.put("value", value);
        params.put("required", required);
        params.put("type", type.getSimpleName());
        return params;
    }

    /**
     * 获取多个的参数
     *
     * @param method
     * @return
     */
    private List<Map<String, Object>> dealParams(Method method) {
        List<Map<String, Object>> list = new ArrayList<>();
        ApiParams annotation = method.getAnnotation(ApiParams.class);
        ApiParams apiParamsAnnotation = method.getAnnotation(ApiParams.class);
        if (annotation != null) {
            ApiParam[] apiParams = annotation.value();
            for (ApiParam apiParam : apiParams) {
                list.add(dealParam(apiParam));
            }
        } else {
            ApiParam apiParam = method.getAnnotation(ApiParam.class);
            if (null != apiParam) {
                list.add(dealParam(apiParam));
            }
        }
        return list;
    }


    /**
     * 获取多个的参数
     *
     * @param method
     * @return
     */
    private List<Map<String, Object>> dealParamsWithAfter(Method method) {
        List<Map<String, Object>> list = new ArrayList<>();
        ApiParams annotation = method.getAnnotation(ApiParams.class);
        if (annotation != null) {
            ApiParam[] apiParams = annotation.value();
            for (ApiParam apiParam : apiParams) {
                list.add(dealParam(apiParam));
            }
        } else {
            ApiParam apiParam = method.getAnnotation(ApiParam.class);
            if (null != apiParam) {
                list.add(dealParam(apiParam));
            }
        }
        return list;
    }

}
