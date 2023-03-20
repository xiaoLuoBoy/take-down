package com.horqian.api.controller.sys;


import cn.shuibo.annotation.Decrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import com.horqian.api.entity.sys.SysRole;
import com.horqian.api.service.sys.SysRoleService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;

/**
 * @author bz
 */
@Slf4j
@Validated
@Api(tags = "角色相关接口")
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysUserService sysUserService;

    private final SysRoleService sysRoleService;

    /**
     * 获取角色列表
     * @param pageUtils
     * @param sysRole
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<SysRole> pageUtils, SysRole sysRole) {

        var qw = new QueryWrapper<SysRole>();

        if (StringUtils.hasText(sysRole.getName()))
            qw.like("name", sysRole.getName());

        var userPage = sysRoleService.page(pageUtils.getPage(), qw);

        return ResultFactory.success(userPage.getRecords(), userPage.getTotal());
    }

    /**
     * 角色下拉列表
     * @return
     */
    @GetMapping("/select")
    public Result select() {

        var qw = new QueryWrapper<SysRole>();
        qw.select("id", "name");

        return ResultFactory.success(sysRoleService.listMaps(qw));
    }

    /**
     * 保存角色
     * @param sysRole
     * @return
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Throwable.class)
    public Result save(@Validated SysRole sysRole) {

        if (sysRole.getId() != null)
            sysRoleService.dealRelate(sysRole);

        var flag = sysRoleService.saveOrUpdate(sysRole);

        if (!flag)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }

    /**
     * 角色删除
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result delete(String id) {

        var idList = Arrays.asList(id.split(","));

        idList.stream().forEach(x -> {
            int count = sysUserService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRoleId, x));
            if (count > 0)
                throw new BaseException("角色已加入到对应用户下，请修改或删除角色后进行删除");
        });

        var del = sysRoleService.removeByIds(idList);

        if (!del)
            throw new BaseException("删除失败");

        return ResultFactory.success();
    }

    @GetMapping("/test")
    public Result test(){
        int a = 1/0;
        return ResultFactory.success();
    }
}
