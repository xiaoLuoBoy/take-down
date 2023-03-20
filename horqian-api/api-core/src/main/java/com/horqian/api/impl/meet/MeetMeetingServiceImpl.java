package com.horqian.api.impl.meet;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.mapper.meet.MeetMeetingMapper;
import com.horqian.api.service.meet.MeetMeetingService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;

/**
 * <p>
 * 会议 - 会议表 服务实现类
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Service
public class MeetMeetingServiceImpl extends ServiceImpl<MeetMeetingMapper, MeetMeeting> implements MeetMeetingService {

    @Override
    public Long getTime(MeetMeeting meetMeeting) {
        long time = 0L;
        // 获取当前时间戳
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // 会议开始时间不为空
        if (StringUtils.hasText(meetMeeting.getMeetingStartTime())) {
            // 会议结束时间不为空
            if (StringUtils.hasText(meetMeeting.getMeetingEndTime())) {
                time = Long.valueOf(meetMeeting.getMeetingEndTime()) - Long.valueOf(meetMeeting.getMeetingStartTime());
            } else {
                time = (timestamp.getTime() - Long.valueOf(meetMeeting.getMeetingStartTime()));
            }
            // 会议结束时间
            if (StringUtils.hasText(meetMeeting.getAgainMeetingTime())) {
                time = time + (timestamp.getTime() - Long.valueOf(meetMeeting.getAgainMeetingTime()));
            }
        }
        // 当前会议时间戳
        return time;
    }
}
