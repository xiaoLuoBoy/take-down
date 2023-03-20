package com.horqian.api.service.meet;

import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.entity.meet.MeetMeeting;


/**
 * <p>
 * 会议 - 会议表 服务类
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
public interface MeetMeetingService extends IService<MeetMeeting> {

    /**
     * 获取会议时间
     * @param meetMeeting
     * @return
     */
    public Long getTime(MeetMeeting meetMeeting);

}
