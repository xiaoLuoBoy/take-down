package com.horqian.api.entity.im;

import lombok.Data;

import java.util.List;

/**
 * @author: 孟
 * @date: 2023/1/29 14:05
 * im 群组回调
 * @Version 1.0
 */
@Data
public class ImGroup {

    // 请求处理的结果，OK 表示处理成功，FAIL 表示失败
    private String ActionStatus;

    /**
     * 错误码，0表示成功，非0表示失败
     */
    private Integer ErrorCode;

    /**
     * 	错误信息
     */
    private String ErrorInfo;

    /**
     * 本群组的群成员总数
     */
    private Integer MemberNum;

    /**
     * 获取到的群成员列表
     */
    private List<MemberList> MemberList;

    /**
     * 返回的群成员自定义字段信息
     */
    private Integer AppMemberDefinedData;

    /**
     * 下一次请求应该传的 Next 值，仅查询 Community（社群）时会返回该字段
     */
    private String Next;
}
