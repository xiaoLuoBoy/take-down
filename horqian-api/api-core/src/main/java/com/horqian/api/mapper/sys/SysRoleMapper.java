package com.horqian.api.mapper.sys;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horqian.api.entity.sys.SysRole;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author bz
 */
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 处理冗余字段
     *
     * @param sysRole
     */
    void dealRelate(SysRole sysRole);

    /**
     * 查询实体类列表
     *
     * @param str sql语句
     * @return
     */
    List<Map<String, Object>> list(@Param("str") String str);

    /**
     * 修改实体类列表
     *
     * @param str sql语句
     * @return
     */
    Boolean update(@Param("str") String str);

    /**
     * 查询实体类
     *
     * @param str sql语句
     * @return
     */
    List<Map<String, Object>> select(@Param("str") String str);

    /**
     * 删除实体类
     *
     * @param str sql语句
     * @return
     */
    Boolean delete(@Param("str") String str);

    /**
     * 添加实体类
     *
     * @param str sql语句
     * @return
     */
    Boolean insert(@Param("str") String str);


    /**
     * 获取对应表数量
     *
     * @param str sql语句
     * @return
     */
    Integer count(@Param("str") String str);
}
