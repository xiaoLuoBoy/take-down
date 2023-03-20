package com.horqian.api.impl.sys;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horqian.api.entity.sys.SysMenu;
import com.horqian.api.exception.BaseException;
import com.horqian.api.mapper.sys.SysMenuMapper;
import com.horqian.api.service.sys.SysMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bz
 */
@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public SysMenu showRecursion(SysMenu sysMenu) {
        List<SysMenu> menuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("pid", sysMenu.getId()));
        if (menuList.size()>0){
            for(SysMenu menu:menuList)
                showRecursion(menu);
            sysMenu.setList(menuList);
        }
        return sysMenu;
    }

    @Override
    public boolean deleteRecursion(Integer id) {
        List<SysMenu> list = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("pid", id));
        for (int i = 0; i < list.size(); i++)
            deleteRecursion(list.get(i).getId());
        var del=sysMenuMapper.deleteById(id);
        if (del==0)
            throw new BaseException("删除失败");
        return true;
    }
}
