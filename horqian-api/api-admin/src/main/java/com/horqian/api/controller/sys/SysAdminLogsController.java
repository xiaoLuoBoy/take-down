package com.horqian.api.controller.sys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horqian.api.entity.sys.SysAdminLogs;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysAdminLogsService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统 - 日志管理 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/sys/sysAdminLogs")
@RequiredArgsConstructor
public class SysAdminLogsController {

    private final SysAdminLogsService sysAdminLogsService;


    /**
     * 日志管理 - 列表
     *
     * @param pageUtils
     * @param sysAdminLogs
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<SysAdminLogs> pageUtils, SysAdminLogs sysAdminLogs) {
        LambdaQueryWrapper<SysAdminLogs> qw = Wrappers.lambdaQuery();
        // 用户名id
        qw.eq(sysAdminLogs.getUserId() != null, SysAdminLogs::getUserId, sysAdminLogs.getUserId());
        // 开始时间
        qw.ge(sysAdminLogs.getStartTime() != null, SysAdminLogs::getTime, sysAdminLogs.getStartTime());
        // 结束时间
        qw.le(sysAdminLogs.getEndTime() != null, SysAdminLogs::getTime, sysAdminLogs.getEndTime());
        // 分页查询
        IPage<SysAdminLogs> page = sysAdminLogsService.page(pageUtils.getPage(), qw);

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }

    /**
     * 日志管理 - 保存
     *
     * @param sysAdminLogs
     * @return
     */
    @PostMapping("/save")
    public Result save(SysAdminLogs sysAdminLogs) {
        boolean bool = sysAdminLogsService.save(sysAdminLogs);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }





}

