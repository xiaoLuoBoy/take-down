package com.horqian.api.dictionaries.sys;

/**
 * 日志是否为错误
 * @author bz
 * @date 2022/2/7
 * @description
 */
public enum LogsErrorEnum {

    FALSE(0),// 否
    TRUE(1);// 是
    public final Integer error;

    LogsErrorEnum(Integer error) {
        this.error = error;
    }

}
