package com.horqian.api.impl.meet;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.horqian.api.entity.meet.MeetInquiriesRecord;
import com.horqian.api.mapper.meet.MeetInquiriesRecordMapper;
import com.horqian.api.service.meet.MeetInquiriesRecordService;
import com.horqian.api.util.WxTrTcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会议 - 问诊记录 服务实现类
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Service
public class MeetInquiriesRecordServiceImpl extends ServiceImpl<MeetInquiriesRecordMapper, MeetInquiriesRecord> implements MeetInquiriesRecordService {

    @Autowired
    WxTrTcUtil wxTrTcUtil;

    public Integer getUserStatus(String userId) {
        // 查寻参会人员在线状态
        String userStatusResult = wxTrTcUtil.getUserStatus(userId);
        JSONObject queryResultobj = JSONUtil.parseObj(userStatusResult);
        JSONArray queryResult = (JSONArray) queryResultobj.get("QueryResult");
        if (queryResult != null) {
            try {
                Object userIds = queryResult.get(0);
                if (userIds != null) {
                    JSONObject userIdsJSON = (JSONObject) userIds;
                    String to_account = (String) userIdsJSON.get("To_Account");
                    if (to_account.equals(userId)) {
                        // 查询状态
                        String Status = (String) userIdsJSON.get("Status");
                        if (Status.equals("Online")) {
                            // 在线
                            return 1;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 离线
        return 0;
    }
}
