package com.xahaolan.emanage.http.bean;

import java.io.Serializable;

/**
 * Created by aiodiy on 2016/12/15.
 */
public class RepBase<T> implements Serializable {
    private Meta meta;
    private T data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}