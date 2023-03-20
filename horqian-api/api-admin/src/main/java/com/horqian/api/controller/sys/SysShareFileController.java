package com.horqian.api.controller.sys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.entity.meet.MeetIminfo;
import com.horqian.api.entity.sys.SysShareFile;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysShareFileService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统 - 共享文件 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-02-09
 */
@RestController
@RequestMapping("/sys/shareFile")
@RequiredArgsConstructor
public class SysShareFileController {

    private final SysUserService sysUserService;

    private final SysShareFileService sysShareFileService;

    /**
     * 系统 - 共享文件列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<SysShareFile> pageUtils) {
        LambdaQueryWrapper<SysShareFile> qw = Wrappers.lambdaQuery();
        // 分页查询 共享文件列表
        IPage<SysShareFile> page = sysShareFileService.page(pageUtils.getPage(), qw);

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }


    /**
     * 系统 - 共享文件保存修改
     *
     * @param sysShareFile 共享文件
     * @return
     */
    @PostMapping("/save")
    public Result save(SysShareFile sysShareFile) {
        // 用户id
        SysUser user = sysUserService.getUser();
        if (sysShareFile.getId() == null) {
            // 上传文件id
            sysShareFile.setId(user.getId());
            // 上传文件
            sysShareFile.setNickname(user.getNickname());
            // 上传时间
            sysShareFile.setTime(LocalDateTime.now());
        }
        boolean bool = sysShareFileService.saveOrUpdate(sysShareFile);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }


    /**
     * 系统 - 共享文件查询
     *
     * @return
     */
    @GetMapping("/select")
    public Result select() {
        LambdaQueryWrapper<SysShareFile> qw = Wrappers.lambdaQuery();
        // 文件列表查询
        List<SysShareFile> list = sysShareFileService.list();
        return ResultFactory.success(list);
    }

    /**
     * 系统 - 共享文件删除
     *
     * @return id 多个逗号分割
     */
    @PostMapping("/delete")
    public Result delete(String id) {
        // 字符串转换为集合
        List<String> ids = Arrays.asList(id.split(","));
        boolean bool = sysShareFileService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");
        return ResultFactory.success();
    }

}

