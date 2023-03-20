package com.horqian.api.dictionaries.meet;

/**
 * @author 孟
 * @date 2023/01/10
 * @description 日志类型枚举
 */
public enum AdminLogsTypeEnum {

    MEETSAVE("/meet/meeting/save", "创建了会议"),
    MEETDELETE("/meet/meeting/delete","删除了会议"),
    MEETCANCEL("/meet/meeting/cancel", "取消了会议"),
    MEETRECORDSAVE("/meet/inquiriesRecord/save", "创建了问诊记录"),
    MEETRECORDDELETE("/meet/inquiriesRecord/delete","删除了问诊记录"),
    MEETVIDEODELETE("/meet/inquiriesVideo/delete", "删除了问诊视频"),
    MEETMINUTESSAVE("/meet/meetingMinutes/save", "创建了会议纪要"),
    MEETMINUTESDELETE("/meet/inquiriesRecord/delete","删除了会议纪要"),
    MEETMINUTESWORD("/meet/inquiriesVideo/delete", "导出了会议纪要"),
    MEETNOTICESAVE("/meet/notice/save", "发布了会议公告"),
    MEETNOTICEDELETE("/meet/inquiriesRecord/delete","删除了会议公告"),
    SYSDEPARTMENTSAVE("/sys/department/save","创建了部门"),
    SYSDEPARTMENTDELETE("/sys/department/delete","删除了部门"),
    SYSROLESAVE("/sys/role/save","创建了角色"),
    SYSROLEDELETE("/sys/department/delete","删除了角色"),
    SYSUSERSAVE("/sys/user/save","创建了用户"),
    SYSUSERPASSWORD("/sys/user/password","修改了密码"),
    SYSUSERDELETE("/sys/user/delete","删除了用户"),
    SYSSHAREFILESAVE("/sys/shareFile/save","上传了共享文件"),
    SYSSHAREFILEDELETE("/sys/shareFile/delete","删除了共享文件"),
    INTSFIRMWARESAVE("/ints/firmware/save","上传了新的固件"),
    INTSFIRMWAREDELETE("/ints/firmware/delete","删除固件");

    public final String method;
    public final String title;

    AdminLogsTypeEnum(String method, String title) {
        this.method = method;
        this.title = title;
    }

    public static AdminLogsTypeEnum adminLogsTypeEnum(String method){
        for (AdminLogsTypeEnum meetingTypeEnum : AdminLogsTypeEnum.values()) {
            if (meetingTypeEnum.method.equals(method))
                return meetingTypeEnum;
        }
        return null;
    }

}
