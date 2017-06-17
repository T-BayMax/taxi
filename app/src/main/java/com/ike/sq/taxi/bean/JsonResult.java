package com.ike.sq.taxi.bean;

/**
 *
 * Created by T-BayMax on 2017/5/16.
 */

public class JsonResult<T> {
    private int retCode;
    private T result;
    private String msg;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
