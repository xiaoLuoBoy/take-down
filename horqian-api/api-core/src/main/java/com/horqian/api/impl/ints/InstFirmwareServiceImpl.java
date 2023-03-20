package com.horqian.api.impl.ints;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.horqian.api.entity.ints.InstFirmware;
import com.horqian.api.mapper.ints.InstFirmwareMapper;
import com.horqian.api.service.ints.InstFirmwareService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 仪器 - 固件版本 服务实现类
 * </p>
 *
 * @author 孟
 * @since 2023-02-10
 */
@Service
public class InstFirmwareServiceImpl extends ServiceImpl<InstFirmwareMapper, InstFirmware> implements InstFirmwareService {

}
