package com.horqian.api.entity.trtc;

import lombok.Data;

/**
 * @author ：kk
 * @date ：Created 2022/9/5
 * @description： trtc 事件信息
 */
@Data
public class EventInfo {

    /**
     * 房间号
     */
    private String RoomId;

    /**
     * 任务id
     */
    private String TaskId;


    private Payload Payload;







}
