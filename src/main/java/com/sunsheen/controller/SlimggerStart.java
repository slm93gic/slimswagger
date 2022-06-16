package com.sunsheen.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.json.JSONUtil;
import com.sunsheen.SlimggerRun;
import com.sunsheen.core.Propertiesgger;
import com.sunsheen.core.SlimggerAchiever;
import com.sunsheen.utils.Result;
import com.sunsheen.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;


/**
 * @Description: (SlimggerStart启动器)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 **/
@SuppressWarnings("all")
public class SlimggerStart extends HttpServlet {

    private static Map<String, Map<String, Object>> apiTimes = new HashMap<>();

    @Override
    public void init() {
        SlimggerRun run = new SlimggerRun(Propertiesgger.class);
        run.start();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Object data = Result.SUCCESS(null);
        String servletPath = request.getRequestURI();


        if (servletPath.contains("slimgger/apis")) {  //获取接口信息
            data = Result.SUCCESS(JSONUtil.parse(SlimggerAchiever.getApis()));

        } else if (servletPath.contains("slimgger/base/info")) { //获取基本表头信息
            data = Result.SUCCESS(Propertiesgger.slimSwaggerPoperties());

        } else if (servletPath.contains("slimgger/refresh")) {  //刷新接口
            SlimggerAchiever.reScanner();

        } else if (servletPath.contains("slimgger/export")) {  //导出数据


        } else if (servletPath.contains("slimgger/run")) { // 执行程序


        } else if (servletPath.contains("slimgger/times")) {//查询api评价查询时间，按降序
            data = Result.SUCCESS(getAvgApiCostTimes(apiTimes));

        } else if (servletPath.contains("slimgger/insert/times")) { //插入api信息
            inner(request);

        }
        out.println(JSONUtil.parse(data).toString());
        out.close();
    }

    /**
     * 获取每个api的花费时间
     *
     * @param apiTimes
     * @return
     */
    private Object getAvgApiCostTimes(Map<String, Map<String, Object>> apiTimes) {
        List<Map<String, Object>> lists = new ArrayList<>();

        for (Entry<String, Map<String, Object>> entrySet : apiTimes.entrySet()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("api", entrySet.getKey());

            Map<String, Object> next = entrySet.getValue();
            temp.put("desc", next.get("meth_desc"));
            temp.put("className", next.get("class_name"));
            List<Long> list = (List<Long>) next.get("time");
            temp.put("time", calculationTime(list));
            temp.put("times", list.size());
            lists.add(temp);
        }
        sortArrays(lists);
        convertTimeDesc(lists);
        return lists;
    }


    /**
     * 转换时间的描述
     *
     * @param lists
     */
    private void convertTimeDesc(List<Map<String, Object>> lists) {
        for (Map<String, Object> map : lists) {
            long time = (long) map.get("time");
            long second = time / DateUnit.SECOND.getMillis();
            long ms = time % DateUnit.SECOND.getMillis();
            if (second == 0) {
                map.put("cost_time", ms + "毫秒");
            } else {
                map.put("cost_time", second + "秒" + ms + "毫秒");
            }
        }
    }

    /**
     * 排序
     *
     * @param list
     */
    private void sortArrays(List<Map<String, Object>> list) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                BigDecimal time1 = new BigDecimal(map1.get("time").toString());
                BigDecimal time2 = new BigDecimal(map2.get("time").toString());
                return time2.compareTo(time1);
            }
        });
    }

    /**
     * 统计时间
     *
     * @param entrySet
     */
    private long calculationTime(List<Long> list) {
        long start = 0l;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            try {
                long temp = list.get(i);
                if (0 != temp) {
                    start += temp;
                    index++;
                }
            } catch (Exception e) {
            }
        }
        return index == 0 ? 0 : start / index;
    }


    /**
     * 插入数据
     *
     * @param request
     */
    private void inner(HttpServletRequest request) {
        Map<String, Object> map = getParameters(request);
        //单个方法的说明和id
        String methDesc = StringUtils.toString(map, "meth_desc");
        String path = StringUtils.toString(map, "path");
        //花费时间
        long time = Long.valueOf(map.get("time").toString());

        inner(methDesc, path, time);
    }

    /**
     * 组装数据
     *
     * @param request
     */
    public void inner(String methDesc, String path, long time) {
        if (path.contains("api/rest")) {
            path = path.split("api/rest")[1];
        }
        Map<String, Object> maps = apiTimes.get(path);
        if (null == maps) {
            inner1(methDesc, path, time);
        } else {
            inner(maps, path, time);
        }
    }


    /**
     * 不存在的序号
     *
     * @param methDesc
     * @param path
     * @param time
     */
    private void inner1(String methDesc, String path, long time) {
        Map<String, Object> maps = new HashMap<>();
        if (StringUtils.isEmpty(methDesc)) {
            List<Map<String, Object>> apis = SlimggerAchiever.getApis();
            if (apis.size() == 0) {
                return;
            }
            setDesc(apis, maps, path);
        }
        List<Long> list = new LinkedList<>();
        list.add(time);
        maps.put("time", list);
        apiTimes.put(path, maps);
    }

    /**
     * 已经存在的序号
     *
     * @param maps
     * @param path
     * @param time
     */
    private void inner(Map<String, Object> maps, String path, long time) {
        Object desc = maps.get("meth_desc");
        if (StringUtils.isEmpty(desc)) {
            List<Map<String, Object>> apis = SlimggerAchiever.getApis();
            if (apis.size() == 0) {
                return;
            }
            setDesc(apis, maps, path);
        }
        List<Long> list = (List<Long>) maps.get("time");
        list.add(time);
    }


    /**
     * 获取接口描述
     *
     * @param apis
     * @param path
     * @return
     */
    private void setDesc(List<Map<String, Object>> lists, Map<String, Object> maps, String path) {
        for (Map<String, Object> map : lists) {
            List<Map<String, Object>> temps = (List<Map<String, Object>>) map.get("values");
            if (temps.toString().contains(path)) {
                for (Map<String, Object> tempMap : temps) {
                    String tempPath = (String) tempMap.get("path");
                    if (tempPath.equals(path)) {
                        maps.put("meth_desc", tempMap.get("descrition"));
                        maps.put("class_name", map.get("className"));
                        return;
                    }
                }
            }
        }
    }


    /**
     * @Description: (获取requert参数)
     * @author: SLM
     * @date: 2020年1月8日 上午11:14:54
     */
    public Map<String, Object> getParameters(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            Object value = request.getParameter(key);
            if (!StringUtils.isEmpty(value)) {
                map.put(key, value);
            }
        }
        return map;
    }


}
