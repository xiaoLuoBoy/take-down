package com.horqian.api.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 系统-短信模板
 * </p>
 *
 * @author bz
 * @since 2021-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_message")
@ApiModel(value="SysMessage对象", description="系统-短信模板")
public class SysMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    @TableField("name")
    private String name;

    /**
     * 模板id
     */
    @ApiModelProperty(value = "模板id")
    @TableField("template_id")
    private String templateId;

    /**
     * 签名
     */
    @ApiModelProperty(value = "签名")
    @TableField("signature")
    private String signature;

    /**
     * 模板参数
     */
    @ApiModelProperty(value = "模板参数")
    @TableField("data")
    private List<String> data;

    /**
     * param 参数   例：手机号码,验证码
     */
    @TableField("param")
    private String param;



    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
