package com.horqian.api.result;

import lombok.Data;

/**
 * @author bz
 */
@Data
public class JsonResult<T> implements Result {

    public String msg = "";

    public Long count;

    public T data;

    public Integer code;

    public String aesStr;


    public JsonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JsonResult(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public JsonResult(Integer code, T data, long count, String msg) {
        this.code = code;
        this.data = data;
        this.count = count;
        this.msg = msg;
    }

    public JsonResult(String aesStr) {
        this.aesStr = aesStr;
    }

}