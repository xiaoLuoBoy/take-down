package com.horqian.api.controller.ints;

import com.horqian.api.exception.BaseException;
import com.horqian.api.mapper.sys.SysRoleMapper;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: 孟
 * @date: 2023/3/1 16:42
 * exe端实体类
 * @Version 1.0
 */
@RestController
@RequestMapping("/entity")
@RequiredArgsConstructor
public class EntityController {

    private final SysRoleMapper sysRoleMapper;


    /**
     * exe端实体类 - 列表
     *
     * @param str
     * @return
     */
    @PostMapping("/list")
    public Result list(String str) {
        List<Map<String, Object>> list = sysRoleMapper.list(str);
        return ResultFactory.success(list);
    }

    /**
     * exe端实体类 - 保存
     *
     * @param str
     * @return
     */
    @PostMapping("/save")
    public Result save(String str) {

        Boolean bool = sysRoleMapper.insert(str);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }

    /**
     * exe端实体类 - 修改
     *
     * @param str
     * @return
     */
    @PostMapping("/update")
    public Result update(String str) {
        Boolean bool = sysRoleMapper.update(str);
        if (!bool)
            throw new BaseException("修改失败");
        return ResultFactory.success();
    }

    /**
     * exe端实体类 - 查询
     *
     * @param str
     * @return
     */
    @PostMapping("/select")
    public Result select(String str) {
        // 实体类查询
        List<Map<String, Object>> list = sysRoleMapper.select(str);
        return ResultFactory.success(list);
    }

    /**
     * exe端实体类 - 获取对应表数据数量
     *
     * @param str
     * @return
     */
    @PostMapping("/count")
    public Result count(String str) {
        Integer count = sysRoleMapper.count(str);
        return ResultFactory.success(count);
    }

    /**
     * exe端实体类 - 删除
     *
     * @param str
     * @return
     */
    @PostMapping("/delete")
    public Result delete(String str) {
        Boolean bool = sysRoleMapper.update(str);
        if (!bool)
            throw new BaseException("删除失败");
        return ResultFactory.success();
    }
}
