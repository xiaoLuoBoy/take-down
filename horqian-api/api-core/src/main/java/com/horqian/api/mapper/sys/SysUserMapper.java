package com.horqian.api.mapper.sys;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horqian.api.entity.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统管理员表	 Mapper 接口
 * </p>
 *
 * @author bz
 * @since 2020-10-19
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
