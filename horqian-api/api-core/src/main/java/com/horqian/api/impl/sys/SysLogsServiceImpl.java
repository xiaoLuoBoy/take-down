package com.horqian.api.impl.sys;


import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horqian.api.dictionaries.sys.*;
import com.horqian.api.entity.sys.SysLogs;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.mapper.sys.SysLogsMapper;
import com.horqian.api.mapper.sys.SysUserMapper;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysLogsService;
import com.horqian.api.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 系统-操作日志 服务实现类
 * </p>
 *
 * @author bz
 * @since 2021-08-24
 */
@Service
@RequiredArgsConstructor
public class SysLogsServiceImpl extends ServiceImpl<SysLogsMapper, SysLogs> implements SysLogsService {

    private final SysUserMapper sysUserMapper;

    private final JwtTokenUtil jwtTokenUtil;

    private final String tokenHeader = "Authorization";
    private final String tokenHead = "Bearer";

    @Override
    public <T> LogsTypeEnum insterOrUpdate(T object) {
        Class cls = object.getClass();
        Method getId = null;
        Integer id = null;
        try {
            getId = cls.getMethod("getId");
            id = (Integer) getId.invoke(object);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        LogsTypeEnum logsTypeEnum = LogsTypeEnum.INSTERT;
        if (id != null)
            logsTypeEnum = LogsTypeEnum.UPDATE;
        return logsTypeEnum;
    }

    @Override
    public void saveSystemLog(String object, Object detail, LogsTypeEnum logsTypeEnum, boolean bool) {
        SysLogs sysLogs = new SysLogs();
        sysLogs.setUserName("定时");
        sysLogs.setSystemType(SysTemTypeEnum.ADMIN.type);
        sysLogs.setTime(LocalDateTime.now());
        sysLogs.setMethod("127.0.0.1");

        this.baseMapper.insert(sysLogs);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAdminError(HttpServletRequest request, String exceptionDetail, SysLogs log) {
        log.setError(LogsErrorEnum.TRUE.error);
        log.setHandle(LogsHandleEnum.TRUE.handle);
        log.setSystemType(SysTemTypeEnum.ADMIN.type);
        log.setExceptionDetail(exceptionDetail);
        assemble(request, log);
        this.baseMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWeappError(HttpServletRequest request, String exceptionDetail, SysLogs log) {
        log.setError(LogsErrorEnum.TRUE.error);
        log.setHandle(LogsHandleEnum.TRUE.handle);
        log.setSystemType(SysTemTypeEnum.WEAPP.type);
        log.setExceptionDetail(exceptionDetail);
        assemble(request, log);
        this.baseMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMqError(HttpServletRequest request, String exceptionDetail, SysLogs log) {
        log.setError(LogsErrorEnum.TRUE.error);
        log.setHandle(LogsHandleEnum.TRUE.handle);
        log.setSystemType(SysTemTypeEnum.MQ.type);
        assemble(request, log);
        this.baseMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAdminLog(HttpServletRequest request) {
        //todo
        SysLogs log = new SysLogs();
        log.setSystemType(SysTemTypeEnum.ADMIN.type);
        assemble(request, log);
        this.baseMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWeappLog(HttpServletRequest request) {
        //todo
        SysLogs log = new SysLogs();
        log.setSystemType(SysTemTypeEnum.WEAPP.type);
        assemble(request, log);
        this.baseMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMqLog(HttpServletRequest request) {
        //todo
        SysLogs log = new SysLogs();
        log.setSystemType(SysTemTypeEnum.MQ.type);
        assemble(request, log);
        this.baseMapper.insert(log);
    }


    /**
     * 组装消息对象
     *
     * @param request
     * @param log
     */
    public void assemble(HttpServletRequest request, SysLogs log) {
        //参数值
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.setRequestIp(request.getRemoteAddr());
        log.setMethod(request.getRequestURI());
        getUser(request, log, SysTemTypeEnum.getSysTemTypeEnumByType(log.getSystemType()));
        log.setParams(JSONUtil.toJsonStr(parameterMap));
        log.setTime(LocalDateTime.now());
    }

    /**
     * 获取用户名称
     *
     * @param request
     * @return
     */
    public Result getUser(HttpServletRequest request, SysLogs log, SysTemTypeEnum sysTemTypeEnum) {
        Integer userId = null;
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            //校验token
            var verify = jwtTokenUtil.verify(authToken);
            if (!verify) {
                return ResultFactory.success("用户未登录");
            }
            userId = JwtTokenUtil.getUserId(authToken);
        }
        if (userId != null) {
            log.setUserId(userId);
            switch (sysTemTypeEnum) {
                case ADMIN -> {
                    SysUser user = sysUserMapper.selectById(userId);
                    log.setUserName(user.getUsername());
                }
                case WEAPP -> {

                }
                default -> log.setUserName("游客");
            }
        } else
            log.setUserName("游客");

        return ResultFactory.success();
    }
}
