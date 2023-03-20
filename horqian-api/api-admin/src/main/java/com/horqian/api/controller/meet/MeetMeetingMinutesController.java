package com.horqian.api.controller.meet;


import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.word.Word07Writer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetMeetingMinutes;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetMeetingMinutesService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 会议 - 会议纪要 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@RestController
@RequestMapping("/meet/meetingMinutes")
@RequiredArgsConstructor
public class MeetMeetingMinutesController {

    private final MeetMeetingMinutesService meetMeetingMinutesService;

    private final SysUserService sysUserService;

    /**
     * 会议纪要 - 列表
     *
     * @param pageUtils
     * @param meetingOnlyId
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<MeetMeetingMinutes> pageUtils, String meetingOnlyId) {
        LambdaQueryWrapper<MeetMeetingMinutes> qw = Wrappers.lambdaQuery();
        // 会议id
        qw.eq(MeetMeetingMinutes::getMeetingOnlyId, meetingOnlyId);
        //// 会议结束时间
        //qw.le(meetMeeting.getEndTime() != null, MeetMeeting::getEndTime, meetMeeting.getEndTime());
        // 会议纪要分页查询
        IPage<MeetMeetingMinutes> page = meetMeetingMinutesService.page(pageUtils.getPage(), qw);
        return ResultFactory.success(page.getRecords(), page.getTotal());
    }

    /**
     * 会议纪要 - 修改保存
     *
     * @param meetMeetingMinutes
     * @return
     */
    @PostMapping("/save")
    public Result save(MeetMeetingMinutes meetMeetingMinutes) {
        if (meetMeetingMinutes.getId() == null) {
            // 获取当前登录用户
            SysUser user = sysUserService.getUser();
            // 用户id
            meetMeetingMinutes.setUserId(user.getId());
            // 用户名称
            meetMeetingMinutes.setUserName(user.getNickname());
            // 用户手机号
            meetMeetingMinutes.setPhone(user.getPhone());
        }

        boolean bool = meetMeetingMinutesService.saveOrUpdate(meetMeetingMinutes);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }

    /**
     * 会议纪要 - 详情
     *
     * @param meetingOnlyId 会议id
     * @param userId        用户id
     * @return
     */
    @GetMapping("/show")
    public Result show(String meetingOnlyId, Integer userId) {
        LambdaQueryWrapper<MeetMeetingMinutes> qw = Wrappers.lambdaQuery();
        // 会议id
        qw.eq(MeetMeetingMinutes::getMeetingOnlyId, meetingOnlyId);
        // 用户id
        qw.eq(MeetMeetingMinutes::getUserId, userId);
        MeetMeetingMinutes serviceOne = meetMeetingMinutesService.getOne(qw);
        return ResultFactory.success(serviceOne);
    }

    /**
     * 会议纪要 - 导出word
     *
     * @param id 会议纪要id
     * @return
     */
    @GetMapping("/word")
    public Result word(HttpServletResponse response, Integer id) throws IOException {
        // 查询会议纪要
        MeetMeetingMinutes meetMeetingMinutes = meetMeetingMinutesService.getById(id);
        // 导出 word
        Word07Writer writer = new Word07Writer();
        // 添加段落（标题）
         writer.addText(new Font("方正小标宋简体", Font.PLAIN, 22), "会议纪要");
         // 根据/n截取数据
        String[] summary = meetMeetingMinutes.getSummary().split("\n");
        List<String> list = Arrays.asList(summary);
        // 添加段落（正文）
        if (!list.isEmpty()) {
            list.stream().forEach(x ->{
                writer.addText(new Font("宋体", Font.PLAIN, 18), x);
            });
        }
        // 创建临时文件
        File file = File.createTempFile("41s" ,".docx");
        System.out.println("打印文件路径:------------------>" + file.getPath());
        // 写出到文件
        writer.flush(file);
        // 关闭写出流
        writer.close();

        InputStream fin = null;
        ServletOutputStream out = null;
        try {
            // 调用工具类WordGenerator的createDoc方法生成Word文档
            fin = new FileInputStream(file);
            // 设置编码格式为 utf - 8
            response.setCharacterEncoding("utf-8");
            // 返回值
            response.setContentType("application/msword");
            // 设置浏览器以下载的方式处理该文件默认名为resume.doc
            response.addHeader("Content-Disposition","attachment;filename="+ "会议纪要" +".doc");
            // 获取输出流
            out = response.getOutputStream();
            // 创建缓冲区
            byte[] buffer = new byte[512];
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } finally {
            if (fin != null)
                fin.close();
            if (out != null)
                out.close();
            if (file != null)
                // 删除临时文件
                file.delete();
        }
          // todo 导出备用方法
//        // 导出到response输出流中
//        ServletOutputStream os = response.getOutputStream();
//        response.setCharacterEncoding("utf-8");
//        response.setContentType("application/msword");
//        String fileName = file.getName() + ".docx";
//        response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
//        writer.flush(os);
//        os.close();
//        writer.close();

        return ResultFactory.success();
    }



    /**
     * 会议 - 删除
     *
     * @return id 多个逗号分割
     */
    @DeleteMapping("/delete")
    public Result delete(String id) {
        List<String> ids = Arrays.asList(id.split(","));
        // 批量删除会议纪要
        boolean bool = meetMeetingMinutesService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");
        return ResultFactory.success();
    }
}

