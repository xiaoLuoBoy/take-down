package com.horqian.api.controller.sys;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.horqian.api.context.UserContext;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.entity.sys.SysMenu;
import com.horqian.api.service.sys.SysMenuService;
import com.horqian.api.service.sys.SysRoleService;
import com.horqian.api.service.sys.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bz
 */
@Slf4j
@Validated
@RestController
@Api(tags = "菜单相关接口")
@AllArgsConstructor
@RequestMapping("/sys/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    /**
     * 获取菜单
     *
     * @return
     */
    @GetMapping("/show")
    public Result show() {

        var sysUser = sysUserService.getById(UserContext.getContext());

        var keywords = sysRoleService.getById(sysUser.getRoleId()).getKeywords();

        var authorities = new ArrayList<GrantedAuthority>();
        Arrays.stream(keywords.split(",")).forEach(s -> authorities.add(new SimpleGrantedAuthority(s)));

        String[] strings = keywords.split(",");
        List<SysMenu> menuList = new ArrayList<>();
        Arrays.stream(strings).forEach(perm -> {
            menuList.add(sysMenuService.getOne(new QueryWrapper<SysMenu>().eq("perm", perm)));
        });
        ArrayList<SysMenu> newMenuList = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu != null) {
                if (menu.getPid() == 0) {
                    menu.setList(new ArrayList<>());
                    for (SysMenu menu2 : menuList) {
                        if (menu2 != null) {
                            if (menu.getId().equals(menu2.getPid())) {
                                menu2.setList(new ArrayList<>());
                                for (SysMenu menu3 : menuList) {
                                    if (menu3 != null) {
                                        if (menu2.getId().equals(menu3.getPid())) {
                                            List<SysMenu> list = menu2.getList();
                                            list.add(menu3);
                                            menu2.setList(list);
                                        }
                                    }
                                }
                                List<SysMenu> list = menu.getList();
                                list.add(menu2);
                                menu.setList(list);
                            }
                        }
                    }
                    newMenuList.add(menu);
                }
            }

        }


        return ResultFactory.success(newMenuList);

    }

    /**
     * 菜单列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result list() {
        var menuList = sysMenuService.list(new QueryWrapper<SysMenu>().eq("pid", 0));
        for (SysMenu menu : menuList)
            //查询递归方法
            sysMenuService.showRecursion(menu);
        return ResultFactory.success(menuList);
    }

    /**
     * 保存菜单
     *
     * @param sysMenu
     * @return
     */
    @PostMapping("/save")
    public Result save(SysMenu sysMenu) {
        boolean bool = sysMenuService.saveOrUpdate(sysMenu);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result delete(Integer id) {
        //删除递归方法
        var del = sysMenuService.deleteRecursion(id);

        if (!del)
            throw new BaseException("删除失败");

        return ResultFactory.success();
    }

}
