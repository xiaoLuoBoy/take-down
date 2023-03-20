package com.horqian.api.service.sys;


import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.dictionaries.sys.LogsTypeEnum;
import com.horqian.api.entity.sys.SysLogs;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 系统-操作日志 服务类
 * </p>
 *
 * @author bz
 * @since 2021-08-24
 */
public interface SysLogsService extends IService<SysLogs> {

    /**
     * 判断修改类型
     * @param object
     * @param <T>
     * @return
     */
    <T> LogsTypeEnum insterOrUpdate(T object);

    /**
     * 未登录情况下
     * @param object 操作对象
     * @param detail 操作内容
     * @param logsTypeEnum 操作类型
     */
    void saveSystemLog(String object, Object detail, LogsTypeEnum logsTypeEnum, boolean bool);

    /**
     * 后台错误日志
     * @param request
     * @param exceptionDetail
     */
    void saveAdminError(HttpServletRequest request, String exceptionDetail, SysLogs log);

    /**
     * 小程序错误日志
     * @param request
     * @param exceptionDetail
     */
    void saveWeappError(HttpServletRequest request, String exceptionDetail, SysLogs log);

    /**
     * 消息队列错误日志
     * @param request
     * @param exceptionDetail
     */
    void saveMqError(HttpServletRequest request, String exceptionDetail, SysLogs log);

    /**
     * 保存后台日志消息
     * @param request
     */
    void saveAdminLog(HttpServletRequest request);

    /**
     * 保存小程序日志消息
     * @param request
     */
    void saveWeappLog(HttpServletRequest request);

    /**
     * 保存消息队列日志消息
     * @param request
     */
    void saveMqLog(HttpServletRequest request);
}
