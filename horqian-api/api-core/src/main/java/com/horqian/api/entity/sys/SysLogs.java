package com.horqian.api.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author bz
 * @date 2022-02-7
 */
@Data
@TableName("sys_logs")
public class SysLogs implements Serializable  {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求ip
     */
    @TableField("request_ip")
    private String requestIp;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 用户名称
     */
    @TableField("user_name")
    private String userName;


    /**
     * 异常信息
     */
    @TableField("exception_detail")
    private String exceptionDetail;

    /**
     * 是否错误 0否 1是
     */
    @TableField(value = "is_error")
    private Integer error;

    /**
     * 方法名
     */
    @TableField("method")
    private String method;

    /**
     * 参数
     */
    @TableField("params")
    private String params;

    /**
     * 操作耗时
     */
    @TableField("consuming_time")
    private Long consumingTime;

    /**
     * 请求时间
     */
    @TableField("time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    /**
     * 系统类型 10后台 20小程序 30消息队列
     */
    @TableField("system_type")
    private Integer systemType;

    /**
     * 是否需要处理 0否 1是
     */
    @TableField("is_handle")
    private Integer handle;

    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
