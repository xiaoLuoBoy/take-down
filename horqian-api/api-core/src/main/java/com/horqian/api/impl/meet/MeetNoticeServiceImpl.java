package com.horqian.api.impl.meet;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.horqian.api.entity.meet.MeetNotice;
import com.horqian.api.mapper.meet.MeetNoticeMapper;
import com.horqian.api.service.meet.MeetNoticeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会议 - 发布公告 服务实现类
 * </p>
 *
 * @author 孟
 * @since 2023-01-12
 */
@Service
public class MeetNoticeServiceImpl extends ServiceImpl<MeetNoticeMapper, MeetNotice> implements MeetNoticeService {

}
