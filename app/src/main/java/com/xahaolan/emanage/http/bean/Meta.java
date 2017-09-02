package com.xahaolan.emanage.http.bean;

import java.io.Serializable;

/**
 * Created by aiodiy on 2017/2/6.
 */

public class Meta implements Serializable {
    private int errCode;
    private String errMsg;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
