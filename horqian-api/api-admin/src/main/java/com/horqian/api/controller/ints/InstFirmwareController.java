package com.horqian.api.controller.ints;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.entity.ints.InstFirmware;
import com.horqian.api.entity.meet.MeetNotice;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.ints.InstFirmwareService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 仪器 - 固件版本 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-02-10
 */
@RestController
@RequestMapping("/ints/firmware")
@RequiredArgsConstructor
public class InstFirmwareController {

    private final SysUserService sysUserService;

    private final InstFirmwareService instFirmwareService;


    /**
     * 固件版本 - 列表
     *
     * @param pageUtils
     * @param instFirmware
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<InstFirmware> pageUtils, InstFirmware instFirmware) {
        LambdaQueryWrapper<InstFirmware> qw = Wrappers.lambdaQuery();
        // 设备名称
        qw.like(StringUtils.hasText(instFirmware.getEquipmentName()), InstFirmware::getEquipmentName, instFirmware.getEquipmentName());
        // 设备类型
        qw.like(StringUtils.hasText(instFirmware.getEquipmentModel()), InstFirmware::getEquipmentModel, instFirmware.getEquipmentModel());
        // 版本号
        qw.like(StringUtils.hasText(instFirmware.getVersionNumber()), InstFirmware::getVersionNumber, instFirmware.getVersionNumber());

        // 分页查询会议公告
        IPage<InstFirmware> page = instFirmwareService.page(pageUtils.getPage(), qw);
        return ResultFactory.success(page.getRecords(), page.getTotal());
    }


    /**
     * 固件版本 - 保存修改
     *
     * @param instFirmware
     * @return
     */
    @PostMapping("/save")
    public Result save(InstFirmware instFirmware) {
        // 保存
        if (instFirmware.getId() == null) {
            SysUser user = sysUserService.getUser();
            instFirmware.setUserId(user.getId());
            instFirmware.setUserName(user.getNickname());
            instFirmware.setTime(LocalDateTime.now());
        }
        boolean bool = instFirmwareService.saveOrUpdate(instFirmware);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }


    /**
     * 固件版本 - 查询
     *
     * @return
     */
    @GetMapping("/select")
    public Result select() {
        LambdaQueryWrapper<InstFirmware> qw = Wrappers.lambdaQuery();
        // 固件版本查询
        List<InstFirmware> list = instFirmwareService.list();
        return ResultFactory.success(list);
    }


    /**
     * 固件版本 - 详情
     *
     * @param id 固件版本id
     * @return
     */
    @GetMapping("/show")
    public Result show(Integer id) {
        LambdaQueryWrapper<InstFirmware> qw = Wrappers.lambdaQuery();
        // 固件版本详情
        InstFirmware instFirmware = instFirmwareService.getById(id);
        return ResultFactory.success(instFirmware);
    }


    /**
     * 固件版本 - 删除
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result delete(String id) {
        List<String> ids = Arrays.asList(id.split(","));
        boolean bool = instFirmwareService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");
        return ResultFactory.success();
    }

}

