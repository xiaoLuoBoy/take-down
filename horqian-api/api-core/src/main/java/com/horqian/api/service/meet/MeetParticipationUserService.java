package com.horqian.api.service.meet;

import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetParticipationUser;

import java.util.List;


/**
 * <p>
 * 会议 - 参会人员表 服务类
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
public interface MeetParticipationUserService extends IService<MeetParticipationUser> {

    /**
     * 移除群组中不在线成员
     *
     * @param groupId   群组id
     */
    void removeGroupUser(String groupId);

    /**
     *
     * 参加 和 创建 会人员发送短信
     * @param meeting
     *
     */
    List<String> smsSend(MeetMeeting meeting);

}
