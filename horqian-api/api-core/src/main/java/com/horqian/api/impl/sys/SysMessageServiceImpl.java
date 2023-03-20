package com.horqian.api.impl.sys;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horqian.api.entity.sys.SysMessage;
import com.horqian.api.mapper.sys.SysMessageMapper;
import com.horqian.api.service.sys.SysMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统-短信模板 服务实现类
 * </p>
 *
 * @author bz
 * @since 2021-07-07
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

}
