package com.horqian.api.dictionaries.meet;

/**
 * @author 孟
 * @date 2023/01/11
 * @description 会议状态枚举
 */
public enum MeetingStatusEnum {

    UNSTARTED(10),//  未开始
    HAVEINHAND(20),// 进行中
    CANCEL(30),    // 已取消
    END(40);// 已结束

    public final Integer type;

    MeetingStatusEnum(Integer type) {
        this.type = type;
    }


}
