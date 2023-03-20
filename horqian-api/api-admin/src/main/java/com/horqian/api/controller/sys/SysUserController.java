package com.horqian.api.controller.sys;


import cn.shuibo.annotation.Decrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horqian.api.entity.ints.InstFirmware;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.util.PageUtils;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.service.sys.SysUserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * @author bz
 */
@Validated
@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private final SysUserService sysUserService;

    @Autowired
    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 用户 - 列表
     *
     * @param pageUtils
     * @param sysUser
     * @return
     */
    @GetMapping("/list")
    public Result list(@ApiIgnore PageUtils<SysUser> pageUtils, @ApiIgnore SysUser sysUser) {

        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        // 用户名
        qw.like(StringUtils.hasText(sysUser.getUsername()), SysUser::getUsername, sysUser.getUsername());
        // 真实姓名
        qw.like(StringUtils.hasText(sysUser.getNickname()), SysUser::getNickname, sysUser.getNickname());
        // 手机号码
        qw.like(StringUtils.hasText(sysUser.getPhone()), SysUser::getPhone, sysUser.getPhone());
        // 部门id
        qw.eq(sysUser.getDepartmentId() != null, SysUser::getDepartmentId, sysUser.getDepartmentId());
        // 性别 0女 1男
        qw.eq(sysUser.getSex() != null, SysUser::getSex, sysUser.getSex());
        // 角色id
        qw.eq(sysUser.getRoleId() != null, SysUser::getRoleId, sysUser.getRoleId());

        IPage<SysUser> page = sysUserService.page(pageUtils.getPage(), qw);

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }


    /**
     * 用户 - 保存
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/save")
    public Result save(@Validated @ApiIgnore SysUser sysUser) {
        var passwordEncoder = new BCryptPasswordEncoder();
        if (sysUser.getId() == null) {
            var sysUserList = sysUserService.list(new QueryWrapper<SysUser>().eq("username", sysUser.getUsername()));
            if (sysUserList.size() > 0)
                throw new BaseException("账号已经存在");
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        } else {
            if (StringUtils.hasText(sysUser.getPassword()))
                sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }

        var flag = sysUserService.saveOrUpdate(sysUser);

        if (!flag)
            throw new BaseException("保存失败");

        return ResultFactory.success();
    }

    /**
     * 用户 - 查询
     *
     * @param username 用户名
     * @param phone    手机号
     * @return
     */
    @GetMapping("/select")
    public Result select(String username, String phone) {
        LambdaQueryWrapper<SysUser> qw = Wrappers.lambdaQuery();
        // 用户名
        qw.like(StringUtils.hasText(username), SysUser::getNickname, username);
        // 手机号
        qw.like(StringUtils.hasText(phone), SysUser::getPhone, phone);
        //qw.select(SysUser::getId, SysUser::getUsername, SysUser::getNickname, SysUser::getPhone);

        List<SysUser> list = sysUserService.list(qw);

        return ResultFactory.success(list);
    }

    /**
     * 用户 - 修改密码
     *
     * @param id
     * @param password
     * @return
     */
    @PostMapping("/password")
    public Result password(@NotNull(message = "id不能为空") Integer id, @NotBlank(message = "密码不能为空") String password) {

        var passwordEncoder = new BCryptPasswordEncoder();

        var sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setPassword(passwordEncoder.encode(password));

        sysUserService.updateById(sysUser);

        return ResultFactory.success();
    }

    /**
     * 用户 - 删除
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result delete(@NotBlank(message = "id不能为空") String id) {

        var idList = Arrays.asList(id.split(","));

        var del = sysUserService.removeByIds(idList);

        if (!del)
            throw new BaseException("删除失败");

        return ResultFactory.success();
    }
}

