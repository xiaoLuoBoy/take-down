package com.horqian.api.dictionaries.meet;

/**
 * @author 孟
 * @date 2023/01/10
 * @description 会议类型
 */
public enum MeetingInquiriesRecordinterviewStatusEnum {

    NOTSTARTED(10),// 未开始
    HAVEINHAND(20),// 问诊中
    END(30),    // 已结束
    ABSENT(40);// 未到人员

    public final Integer interviewStatus;


    MeetingInquiriesRecordinterviewStatusEnum(Integer interviewStatus) {
        this.interviewStatus = interviewStatus;
    }


}
