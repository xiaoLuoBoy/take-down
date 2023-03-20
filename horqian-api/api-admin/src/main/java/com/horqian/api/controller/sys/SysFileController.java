package com.horqian.api.controller.sys;


import cn.shuibo.annotation.Decrypt;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horqian.api.entity.ints.InstFirmware;
import com.horqian.api.entity.sys.SysFile;
import com.horqian.api.impl.sys.SysFileServiceImpl;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.ints.InstFirmwareService;
import com.horqian.api.service.sys.SysFileService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统 - 文件 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-02-20
 */
@RestController
@RequestMapping("/sys/file")
@RequiredArgsConstructor
public class SysFileController {

    private final SysFileService sysFileService;


    /**
     * 文件 - 查询
     *
     * @param pageUtils
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<SysFile> pageUtils) {
        IPage<SysFile> page = sysFileService.page(pageUtils.getPage());
        return ResultFactory.success(page.getRecords(), page.getTotal());
    }




    @PostMapping("/delete")
    public Result delete() {
        return ResultFactory.success();
    }

}

