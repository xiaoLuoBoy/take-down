package com.horqian.api.entity.trtc;

import lombok.Data;

/**
 * @author ：kk
 * @date ：Created 2022/9/5
 * @description： trtc回调dto
 */
@Data
public class TrtcCallbackDto {

    /**
     * 事件组 ID
     * EVENT_GROUP_ROOM   1 房间事件组
     * EVENT_GROUP_MEDIA  2 媒体事件组
     */
    private Long EventGroupId;

    /**
     * 回调通知的事件类型
     */
    private Long EventType;


    /**
     * 事件回调服务器向您的服务器发出回调请求的 Unix 时间戳，单位为毫秒
     */
    private Long CallbackTs;

    /**
     * 事件信息
     */
    private EventInfo eventInfo;



}
