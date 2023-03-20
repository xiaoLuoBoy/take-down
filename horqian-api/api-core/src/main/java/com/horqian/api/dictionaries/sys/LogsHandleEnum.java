package com.horqian.api.dictionaries.sys;

/**
 * @author bz
 * @date 2022/02/08
 * @description
 */
public enum LogsHandleEnum {
    FALSE(0),// 否
    TRUE(1);// 是
    public final Integer handle;

    LogsHandleEnum(Integer handle) {
        this.handle = handle;
    }
}
