package com.horqian.api.config.exception;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.horqian.api.entity.sys.SysLogs;
import com.horqian.api.exception.ExceptionHandlers;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysAdminLogsService;
import com.horqian.api.service.sys.SysLogsService;
import com.horqian.api.util.RequestHolder;
import com.horqian.api.util.ThrowableUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;


/**
 * @author bz
 */
@RequiredArgsConstructor
@RestControllerAdvice
public class AdminExceptionHandlers extends ExceptionHandlers implements ResponseBodyAdvice<Object>{

    private final SysLogsService sysLogsService;

    private final SysAdminLogsService sysAdminLogsService;

    private long currentTime = 0L;

    @Value("${msg.qywx.exception.log}")
    private boolean log;

    @Value("${msg.qywx.exception.url}")
    private String qywxUrl;

    @Value("${msg.qywx.exception.package-name}")
    private String packageName;

    @Value("${msg.qywx.exception.content}")
    private String content;

    @Override
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(value = Throwable.class)
    public Result errorHandler(Throwable throwable) {
        SysLogs log = new SysLogs();
        sysLogsService.saveAdminError(RequestHolder.getHttpServletRequest(), new String(ThrowableUtil.getStackTrace(throwable).getBytes()), log);
        push(log);
        return ResultFactory.error(throwable.getMessage());
    }


    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        sysLogsService.saveAdminLog(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        sysAdminLogsService.saveAdminLog(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        currentTime = System.currentTimeMillis();
        //sysLogsService.saveAdminLog(serverHttpRequest);
        return null;
    }

    public Object afterBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        currentTime = System.currentTimeMillis();
        //sysLogsService.saveAdminLog(serverHttpRequest);
        sysAdminLogsService.saveAdminLog(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());

        return null;
    }


//    @InitBinder
//    public void initBinder(HttpServletRequest request){
//        sysLogsService.saveAdminLog(request);
//    }

    public void push(SysLogs sysLogs){
        if(sysLogs.getParams().length() > 2 ){
            if (log){
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                JSONObject paramJson = new JSONObject();
                paramJson.set("msgtype", "markdown");
                JSONObject markdown = new JSONObject();
                markdown.set("content", content + "出现报错\n " +
                        " >异常日志id:<font color=\"comment\"> " + sysLogs.getId() +"</font>\n" +
                        " >异常接口:<font color=\"comment\"> "+ sysLogs.getMethod() +"</font>\n" +
                        " >异常传参:<font color=\"comment\"> "+ sysLogs.getParams() +"</font>\n" +
                        " >异常信息:<font color=\"comment\"> "+ sysLogs.getExceptionDetail().split("at")[0] +"</font>\n" +
                        " >异常位置:<font color=\"comment\"> "+ sysLogs.getExceptionDetail().split(packageName)[1].split("at")[0] +"</font>\n" +
                        " >异常时间:<font color=\"comment\"> "+ df.format(sysLogs.getTime()) +"</font>");
                System.out.println("markdown.size()" + markdown.toString().length());
                paramJson.set("markdown", markdown);
                HttpResponse execute = HttpRequest.post(qywxUrl)
                        .body(paramJson.toString())
                        .execute();
                byte[] bytes = execute.body().getBytes(StandardCharsets.UTF_8);
                String s = new String(bytes);
                System.err.println(s);
            }
        }
    }
}
