package com.sunsheen.utils;

/**
 * @Description: (返回结果集)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 **/
public class Result {

    private int code;
    private Object msg;
    private Object data;

    private Result(int code, Object msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private Result(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public static Result SUCCESS(Object data) {
        return new Result(400, "成功", data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
