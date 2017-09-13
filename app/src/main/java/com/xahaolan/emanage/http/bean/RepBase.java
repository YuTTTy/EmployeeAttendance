package com.xahaolan.emanage.http.bean;

import java.io.Serializable;

/**
 * Created by aiodiy on 2016/12/15.
 */
public class RepBase<T> implements Serializable {
    private Boolean success;
    private String msg;
    private T obj;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}