package com.horqian.api.entity.trtc;

import lombok.Data;

@Data
public class Payload {

    /**
     * 0：代表本录制文件正常上传至点播平台
     * 1：代表本录制文件滞留在服务器或者备份存储上
     * 2：代表本录制文件上传点播任务异常
     */
    private Long Status;


    /**
     * statue 不为0时，对应的错误信息
     */
    private String Errmsg;


    /**
     * 主体信息
     */
    private TencentVod TencentVod;


}
