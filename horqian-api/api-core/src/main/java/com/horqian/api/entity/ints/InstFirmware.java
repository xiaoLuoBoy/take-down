package com.horqian.api.entity.ints;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 仪器 - 固件版本
 * </p>
 *
 * @author 孟
 * @since 2023-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inst_firmware")
public class InstFirmware implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 新版本号
     */
    @TableField("version_number")
    private String versionNumber;

    /**
     * 设备类型id
     */
    @TableField("equipment_type_id")
    private Integer equipmentTypeId;

    /**
     * 设备类型
     */
    @TableField("equipment_type")
    private String equipmentType;

    /**
     * 设备名称
     */
    @TableField("equipment_name")
    private String equipmentName;

    /**
     * 设备id
     */
    @TableField("equipment_id")
    private Integer equipmentId;

    /**
     * 设备型号
     */
    @TableField("equipment_model")
    private String equipmentModel;

    /**
     * 设备生产厂家
     */
    @TableField("equipment_manufactor")
    private String equipmentManufactor;

    /**
     * 设备mac地址
     */
    @TableField("equipment_serial_number")
    private String equipmentSerialNumber;

    /**
     * 版本号（当前版本）
     */
    @TableField("equipment_edittion")
    private String equipmentEdittion;

    /**
     * 更新内容
     */
    @TableField("content")
    private String content;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 文件链接
     */
    @TableField("url")
    private String url;

    /**
     * 时间
     */
    @TableField("time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;



}
