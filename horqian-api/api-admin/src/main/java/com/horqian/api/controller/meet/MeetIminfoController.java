package com.horqian.api.controller.meet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.entity.meet.MeetIminfo;
import com.horqian.api.entity.meet.MeetNotice;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetIminfoService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 会议 - im即时消息 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-01-12
 */
@RestController
@RequestMapping("/meet/iminfo")
@RequiredArgsConstructor
public class MeetIminfoController {

    private final MeetIminfoService meetIminfoService;


    /**
     * 会议 - im即时消息列表
     *
     * @param meetIminfo im 及时通讯
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<MeetIminfo> pageUtils, MeetIminfo meetIminfo) {
        LambdaQueryWrapper<MeetIminfo> qw = Wrappers.lambdaQuery();
        // 会议id
        qw.eq(MeetIminfo::getMeetingOnlyId, meetIminfo.getMeetingOnlyId());
        // 发送方
        if (StringUtils.hasText(meetIminfo.getSender()) && StringUtils.hasText(meetIminfo.getReceiver())) {
            // 发送方 接收方
            qw.and(wrapper -> wrapper.or(i ->i.eq(StringUtils.hasText(meetIminfo.getSender()), MeetIminfo::getSender, meetIminfo.getSender()).eq(StringUtils.hasText(meetIminfo.getReceiver()), MeetIminfo::getReceiver, meetIminfo.getReceiver()))
                    .or(x ->  x.eq(StringUtils.hasText(meetIminfo.getReceiver()), MeetIminfo::getSender, meetIminfo.getReceiver()).eq(StringUtils.hasText(meetIminfo.getSender()) , MeetIminfo::getReceiver, meetIminfo.getSender())));
        }
        // 根据发送时间排序
        qw.orderByDesc(MeetIminfo::getSendTime);
        // 分页查询 im 及时消息列表
        IPage<MeetIminfo> page = meetIminfoService.page(pageUtils.getPage() , qw);
        // 查询出来的时间按发送时间正序排序
        List<MeetIminfo> meetIminfoList = new ArrayList<>();
        if (!page.getRecords().isEmpty()) {
            meetIminfoList =  page.getRecords().stream().sorted(Comparator.comparing(MeetIminfo::getSendTime)).collect(Collectors.toList());
        }
        return ResultFactory.success(!page.getRecords().isEmpty() ? meetIminfoList : page.getRecords(), page.getTotal());
    }


    /**
     * 会议 - im即时消息保存
     *
     * @param meetIminfo 即时消息
     * @return
     */
    @PostMapping("/save")
    public Result save(MeetIminfo meetIminfo) {
        // 发送时间
        meetIminfo.setSendTime(LocalDateTime.now());
        // 保存im即时通讯消息
        boolean bool = meetIminfoService.save(meetIminfo);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }



    /**
     * 会议 - im即时消息删除
     *
     * @return id 多个逗号分割
     */
    @DeleteMapping("/delete")
    public Result delete(String id) {
        // 字符串转换为集合
        List<String> ids = Arrays.asList(id.split(","));
        boolean bool = meetIminfoService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");
        return ResultFactory.success();
    }

}

