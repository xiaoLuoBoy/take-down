package com.horqian.api.controller.meet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horqian.api.dictionaries.meet.MeetingTypeEnum;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetNotice;
import com.horqian.api.entity.meet.MeetParticipationUser;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetNoticeService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 会议 - 发布公告 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-01-12
 */
@RestController
@RequestMapping("/meet/notice")
@RequiredArgsConstructor
public class MeetNoticeController {

    private final SysUserService sysUserService;

    private final MeetNoticeService meetNoticeService;


    /**
     * 会议 - 发布公告列表
     *
     * @param pageUtils     分页
     * @param meetNotice    会议公告
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<MeetNotice> pageUtils, MeetNotice meetNotice) {
        LambdaQueryWrapper<MeetNotice> qw = Wrappers.lambdaQuery();
        // 分页查询会议公告
        IPage<MeetNotice> page = meetNoticeService.page(pageUtils.getPage(), qw);
        return ResultFactory.success(page.getRecords(), page.getTotal());
    }


    /**
     * 会议 - 发布公告保存修改
     *
     * @param meetNotice
     * @return
     */
    @PostMapping("/save")
    public Result save(MeetNotice meetNotice) {
        // 保存
        if (meetNotice.getId() == null) {
            SysUser user = sysUserService.getUser();
            meetNotice.setUserId(user.getId());
            meetNotice.setUserName(user.getNickname());
            meetNotice.setPhone(user.getPhone());
        }
        boolean bool = meetNoticeService.saveOrUpdate(meetNotice);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }



    /**
     * 会议 - 发布公告详情
     *
     * @param meetingOnlyId 会议id
     * @return
     */

    @GetMapping("/show")
    public Result show(String meetingOnlyId) {
        LambdaQueryWrapper<MeetNotice> qw = Wrappers.lambdaQuery();
        qw.eq(MeetNotice::getMeetingOnlyId, meetingOnlyId);
        MeetNotice meetNotice = meetNoticeService.getOne(qw);
        return ResultFactory.success(meetNotice);
    }

    /**
     * 会议 - 发布公告删除
     *
     * @return id 多个逗号分割
     */
    @DeleteMapping("/delete")
    public Result delete(String id) {
        List<String> ids = Arrays.asList(id.split(","));
        boolean bool = meetNoticeService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");
        return ResultFactory.success();
    }

}

