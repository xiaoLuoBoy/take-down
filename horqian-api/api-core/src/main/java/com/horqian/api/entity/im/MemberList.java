package com.horqian.api.entity.im;

import lombok.Data;

/**
 * @author: 孟
 * @date: 2023/1/29 14:09
 * 群组成员信息
 * @Version 1.0
 */
@Data
public class MemberList {

    /**
     * 用户id
     */
    private String Member_Account;

    /**
     * 群内身份
     */
    private String Role;

    /**
     * 入群时间
     */
    private Integer JoinTime;

    /**
     * 账号
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;
}
