package com.horqian.api.impl.meet;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.horqian.api.dictionaries.meet.MeetingTypeEnum;
import com.horqian.api.entity.im.ImDto;
import com.horqian.api.entity.im.ImGroup;
import com.horqian.api.entity.im.QueryResult;
import com.horqian.api.entity.meet.MeetInquiriesRecord;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetParticipationUser;
import com.horqian.api.entity.sys.SysMessage;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.mapper.meet.MeetParticipationUserMapper;
import com.horqian.api.service.meet.MeetInquiriesRecordService;
import com.horqian.api.service.meet.MeetParticipationUserService;
import com.horqian.api.service.sys.SysMessageService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.WxTrTcUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 会议 - 参会人员表 服务实现类
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Service
@RequiredArgsConstructor
public class MeetParticipationUserServiceImpl extends ServiceImpl<MeetParticipationUserMapper, MeetParticipationUser> implements MeetParticipationUserService {

    private final WxTrTcUtil wxTrTcUtil;

    private final SysUserService sysUserService;

    private final StringRedisTemplate stringRedisTemplate;

    private final SysMessageService sysMessageService;

    private final MeetInquiriesRecordService meetInquiriesRecordService;

    @Override
    public void removeGroupUser(String groupId) {
        // 获取用户
        String userList = wxTrTcUtil.getUserList(groupId);
        // 查询会议人数
        ImGroup imGroup = JSON.parseObject(userList, ImGroup.class);
        // 用户id
        List<String> list = new ArrayList<>();
        if (imGroup.getMemberList() != null && !imGroup.getMemberList().isEmpty()) {
            // 循环获得用户群组id
            imGroup.getMemberList().stream().forEach(x -> {
                list.add(x.getMember_Account());
            });
            // 获取在线人数结果
            String result = wxTrTcUtil.groupUserStatus(list);
            // 获取用户在线状态
            ImDto imDto = JSON.parseObject(result, ImDto.class);
            // 获取用户在线状态
            List<QueryResult> queryResultList = imDto.getQueryResult();
            // 获取用户状态
            List<String> userStatusList = new ArrayList<>();
            queryResultList.stream().forEach(x -> {
                // 用户不是在线状态移除群组
                if (!x.getStatus().equals("Online")) {
                    userStatusList.add(x.getTo_Account());
                    // 分割用户id 判断是否为流
                    String[] to_accountSpilt = x.getTo_Account().split("_");
                    // 外部用户下线
                    if (to_accountSpilt[1].equals("out")) {
                        // 游客
                        stringRedisTemplate.opsForHash().delete("im_" + to_accountSpilt[3], to_accountSpilt[to_accountSpilt.length - 1]);
                    }
                }
            });
            // 批量移除不在线用户
            wxTrTcUtil.batchRemoveGroupUser(groupId, userStatusList);

        }
    }

    @Override
    public List<String> smsSend(MeetMeeting meeting) {
        // 会议创建人id 会议管理员id添加
        List<Integer> integerList = CollectionUtil.newArrayList(meeting.getAdminUserId(),meeting.getUserId());
        // 手机手机号
        List<String> listPhone = sysUserService.listByIds(integerList).stream().filter(x -> StringUtils.hasText(x.getPhone())).map(SysUser::getPhone).collect(Collectors.toList());

        if (meeting.getType().equals(MeetingTypeEnum.INQUIRIES.type)) {
            // 在线问诊
            List<String> recordList = meetInquiriesRecordService.list(new LambdaQueryWrapper<MeetInquiriesRecord>()
                    .eq(MeetInquiriesRecord::getMeetingOnlyId, meeting.getOnlyId())).stream().filter(x -> StringUtils.hasText(x.getPhone()))
                    .map(MeetInquiriesRecord::getPhone).collect(Collectors.toList());
            // 添加集合
            listPhone.addAll(recordList);

        } else {
            // 内外部会议 参会人员列表
            List<String> participationUserList = this.list(new LambdaQueryWrapper<MeetParticipationUser>()
                    .eq(MeetParticipationUser::getMeetingOnlyId, meeting.getOnlyId())).stream().filter(x -> StringUtils.hasText(x.getPhone()))
                    .map(MeetParticipationUser::getPhone).collect(Collectors.toList());
            // 添加集合
            listPhone.addAll(participationUserList);
        }
        // 手机列表集合
        List<String> list = new ArrayList<>();
        // 对集合去重 并加上 +86(标识中国号码)
        listPhone.stream().distinct().forEach(x -> {
            list.add("+86" + x);
        });
        return list;
    }


}
