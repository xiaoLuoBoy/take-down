package com.horqian.api.dictionaries.sys;

/**
 * @author bz
 * @date 2021/08/30
 * @description 日志类型
 */
public enum LogsTypeEnum {
    INSTERT(0),// 新建
    UPDATE(1),// 编辑
    DELETE(2),// 删除
    EXAMINE(3),// 审核
    REJECT(4),// 驳回
    UPLOAD(5),// 上传
    DOWNLOAD(6),// 下载
    SCHEDULED(7);// 定时
    public final Integer type;

    LogsTypeEnum(Integer type) {
        this.type = type;
    }
}
