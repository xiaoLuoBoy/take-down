package com.horqian.api.dictionaries.sys;

/**
 * @author: 孟
 * @date: 2023/2/23 9:42
 * @Version 1.0
 * 短信发送通知
 */
public enum MessageSendEnum {

    VERIFICATIONCODE(1, "中电41所远程设备调试项目 - 验证码", "1710988"),
    MEETINGSTARTTIME15M(2, "中电41所远程设备调试项目 - 会议开始前15分钟提醒", "1710991"),
    MEETINGUPDATETIME(3, "中电41所远程设备调试项目 - 修改会议时间", "1710994"),
    MEETINGCANCEL(4, "中电41所远程设备调试项目 - 取消会议", "1711000"),
    INQUIRYSTARTTIME15(5, "中电41所远程设备调试项目 - 在线问诊开始前15分钟", "1711002"),
    INQUIRYUPDATETIME(6, "中电41所远程设备调试项目 - 修改问诊时间", "1711004"),
    INQUIRYCANCEL(7, "中电41所远程设备调试项目 - 取消问诊", "1711006"),
    MEETINGCREATE(8, "中电41所远程设备调试项目 - 创建会议(早8-晚9)", "1711009"),
    GETMEETINGSTARTTIME24H(9, "中电41所远程设备调试项目 - 会议开始前24小时", "1711017");


    public final Integer id;

    public final String name;

    public final String templateId;


    MessageSendEnum(Integer id, String name, String templateId) {
        this.id = id;
        this.name = name;
        this.templateId = templateId;
    }

}
