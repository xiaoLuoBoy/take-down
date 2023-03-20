package com.horqian.api.config.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.horqian.api.entity.meet.MeetInquiriesRecord;

import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;

import com.horqian.api.service.meet.MeetInquiriesRecordService;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * @author: 孟
 * @date: 2022/4/6 15:03
 * @Version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelUtils {

    private final MeetInquiriesRecordService meetInquiriesRecordService;



    /**
     * 会议在线问诊 - 导出
     *
     * @param response
     * @return
     * @throws IOException
     */
    @PostMapping("/export/inquiries")
    public Result exportInquiries(HttpServletResponse response, String id) throws IOException {
        ArrayList<Map<String, Object>> rows = CollUtil.newArrayList();

        LambdaQueryWrapper<MeetInquiriesRecord> qw = new LambdaQueryWrapper<>();
        // 根据id导出
        qw.in(id != null ,MeetInquiriesRecord::getId, Arrays.asList(id.split(",")));
        // 根据报名顺序排序
        qw.orderByAsc(MeetInquiriesRecord::getSort);
        List<MeetInquiriesRecord> list = meetInquiriesRecordService.list(qw);

        if (list.size() == 0)
            throw new BaseException("无数据");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        list.stream().forEach(x -> {
            Map<String, Object> row1 = new LinkedHashMap<>();
            row1.put("排序", x.getSort());
            row1.put("姓名", x.getName());
            row1.put("性别", x.getSex() == 1? "男" : "女");
            row1.put("电话号码", x.getPhone());
            row1.put("提交时间", df.format(x.getSubmitTime()));
            row1.put("问题描述", x.getDetail());
            rows.add(row1);
        });
        StringBuilder stringBuilder = new StringBuilder();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 设置标题
        writer.merge(rows.get(0).size() - 1, "问诊记录");
        // 仅输出有别名的字段
        writer.setOnlyAlias(true);
        // 导出 excel 表格
        writer.write(rows, true);
        // 设置字体是否居中
        writer.getStyleSet().setAlign(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        writer.setColumnWidth(0, 30);
        writer.setColumnWidth(1, 30);
        writer.setColumnWidth(2, 30);
        writer.setColumnWidth(3, 30);
        writer.setColumnWidth(4, 30);
        writer.setColumnWidth(5, 60);

        // 默认高度设置
        writer.setDefaultRowHeight(30);
        String fileName = URLEncoder.encode(stringBuilder.toString(), "UTF-8");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + df.format(LocalDateTime.now()) + ".xlsx");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out);
        writer.close();
        IoUtil.close(out);

        return ResultFactory.success();
    }

}
