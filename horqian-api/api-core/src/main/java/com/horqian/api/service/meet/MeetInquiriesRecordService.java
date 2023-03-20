package com.horqian.api.service.meet;

import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.entity.meet.MeetInquiriesRecord;


/**
 * <p>
 * 会议 - 问诊记录 服务类
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
public interface MeetInquiriesRecordService extends IService<MeetInquiriesRecord> {

    /**
     * 查询在线状态
     *
     * @param userId
     * @return
     */
    public Integer getUserStatus(String userId);

}
