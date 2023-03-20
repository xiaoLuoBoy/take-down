package com.horqian.api.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.entity.sys.SysAdminLogs;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 * 系统 - 日志管理 服务类
 * </p>
 *
 * @author 孟
 * @since 2023-02-13
 */
public interface SysAdminLogsService extends IService<SysAdminLogs> {

    /**
     * 保存后台用户操作日志
     * @param request
     */
    void saveAdminLog(HttpServletRequest request);


}
