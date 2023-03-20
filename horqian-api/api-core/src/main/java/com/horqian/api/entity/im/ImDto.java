package com.horqian.api.entity.im;

import lombok.Data;

import java.util.List;

@Data
public class ImDto {

    /**
     * 固定为：State.StateChange
     */
    private String CallbackCommand;

    /**
     * 触发本次回调的时间戳，单位为毫秒。
     */
    private String EventTime;

    /**
     * 用户上下线信息
     */
    private InfoDto Info;

    /**
     * 批量获得用户信息
     */
    private List<QueryResult> QueryResult;


}
