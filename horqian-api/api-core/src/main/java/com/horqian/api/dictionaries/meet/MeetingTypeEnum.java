package com.horqian.api.dictionaries.meet;

/**
 * @author 孟
 * @date 2023/01/10
 * @description 会议类型
 */
public enum MeetingTypeEnum {

    INSIDE(10, "inside"),// 内部会议
    EXTERNAL(20,"external"),// 外部会议
    INQUIRIES(30, "quiries");// 问诊会议

    public final Integer type;
    public final String title;

    MeetingTypeEnum(Integer type, String title) {
        this.type = type;
        this.title = title;
    }

    public static MeetingTypeEnum meetingTypeEnum(Integer type){
        for (MeetingTypeEnum meetingTypeEnum : MeetingTypeEnum.values()) {
            if (meetingTypeEnum.type == type)
                return meetingTypeEnum;
        }
        return null;
    }

}
