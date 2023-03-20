package com.horqian.api.controller.sys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horqian.api.entity.meet.MeetInquiriesVideo;
import com.horqian.api.entity.sys.SysDepartment;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysDepartmentService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统 - 部门 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/sys/department")
@RequiredArgsConstructor
public class SysDepartmentController {

    private final SysUserService sysUserService;

    private final SysDepartmentService sysDepartmentService;

    /**
     * 系统 - 部门列表
     *
     * @param pageUtils
     * @param sysDepartment
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<SysDepartment> pageUtils, SysDepartment sysDepartment) {

        LambdaQueryWrapper<SysDepartment> qw = Wrappers.lambdaQuery();

        IPage<SysDepartment> page = sysDepartmentService.page(pageUtils.getPage(), qw);
        return ResultFactory.success(page.getRecords(), page.getTotal());
    }

    /**
     * 系统 - 部门保存修改
     *
     * @param sysDepartment
     * @return
     */
    @PostMapping("/save")
    public Result save(SysDepartment sysDepartment) {
        // 部门
        if (sysDepartment.getId() != null) {
            // 查询部门
            SysDepartment department = sysDepartmentService.getById(sysDepartment.getId());
            // 部门名称
            if (!department.getName().equals(sysDepartment.getName())) {
                LambdaUpdateWrapper<SysUser> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(SysUser::getPhone, sysDepartment.getName());
                updateWrapper.eq(SysUser::getDepartmentId, sysDepartment.getId());
                // 更改对应用户表
                sysUserService.update(updateWrapper);
            }
        }
        boolean bool = sysDepartmentService.saveOrUpdate(sysDepartment);
        if (!bool)
            throw new BaseException("保存失败");

        return ResultFactory.success();
    }

    /**
     * 系统 - 部门查询
     *
     * @return
     */
    @GetMapping("/select")
    public Result select() {
        LambdaQueryWrapper<SysDepartment> qw = Wrappers.lambdaQuery();
        // 部门列表
        List<SysDepartment> list = sysDepartmentService.list(qw);
        return ResultFactory.success(list);
    }


    /**
     * 系统 - 部门删除
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result delete(String id) {
        // 根据部门名称分组
        Map<Integer, List<SysUser>> sysUserList = sysUserService.list().stream().collect(Collectors.groupingBy(SysUser::getDepartmentId));

        var ids = Arrays.asList(id.split(","));

        ids.stream().forEach(x -> {
            // 用户列表
            List<SysUser> list = sysUserList.get(x);
            if (list.size() > 0)
                throw new BaseException("请删除部门下用户之后在删除对应部门");
        });
        return ResultFactory.success();
    }


}

