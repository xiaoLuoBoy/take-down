package com.horqian.api.controller.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.horqian.api.dictionaries.sys.LogsHandleEnum;
import com.horqian.api.entity.sys.SysLogs;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysLogsService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author bz
 * @date 2021/09/01
 * @description
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/logs")
public class SysLogsController {

    private final SysLogsService sysLogsService;

    private final SysUserService sysUserService;

    /**
     * 操作记录列表
     * @param pageUtils
     * @param userId 用户id
     * @param systemType 系统类型 10后台 20小程序 30消息队列
     * @param error 是否错误 0否 1是
     * @param handle 是否需要处理 0否 1是
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<SysLogs> pageUtils, Integer userId, Integer systemType,Integer error, Integer handle, String start, String end){
        QueryWrapper<SysLogs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.eq(systemType != null, "system_type", systemType);
        queryWrapper.eq(error != null, "is_error", error);
        queryWrapper.eq(handle != null, "is_handle", handle);
        queryWrapper.ge(StringUtils.hasText(start),"time",start);
        queryWrapper.le(StringUtils.hasText(end),"time",end);

        IPage<SysLogs> iPage = sysLogsService.page(pageUtils.getPage(), queryWrapper);
        return ResultFactory.success(iPage.getRecords(),iPage.getTotal());
    }

    /**
     * 删除对应的记录
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Throwable.class)
    public Result delete(String id){
        String[] split = id.split(",");
        for (String s:split){
            boolean bool = sysLogsService.removeById(s);
            if (!bool){
                throw new BaseException("删除失败");
            }
        }
        return ResultFactory.success();
    }

    /**
     * 批量处理日志
     * @param id
     * @return
     */
    @PostMapping("/handle")
    @Transactional(rollbackFor = Throwable.class)
    public Result handle(String id){
        ArrayList<SysLogs> list = new ArrayList<>();
        String[] split = id.split(",");
        for (String s:split){
            SysLogs sysLogs = new SysLogs();
            sysLogs.setId(Long.valueOf(s));
            sysLogs.setHandle(LogsHandleEnum.FALSE.handle);
            list.add(sysLogs);
        }
        if (!sysLogsService.updateBatchById(list))
            throw new BaseException("批量处理失败");
        return ResultFactory.success();
    }
}
