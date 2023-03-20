package com.horqian.api.dictionaries.sys;

/**
 * @author bz
 * @date 2021/08/30
 * @description 系统类型
 */
public enum SysTemTypeEnum {
    ADMIN(10),// 后台
    WEAPP(20),// 小程序
    MQ(30);// 消息队列
    public final Integer type;

    SysTemTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static SysTemTypeEnum getSysTemTypeEnumByType(Integer type){
        for(SysTemTypeEnum sysTemTypeEnum : SysTemTypeEnum.values())
            if(type == sysTemTypeEnum.type)
                return sysTemTypeEnum;
        return null;
    }
}
