package com.horqian.api.impl.sys;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horqian.api.dictionaries.meet.AdminLogsTypeEnum;
import com.horqian.api.entity.sys.SysAdminLogs;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.mapper.sys.SysAdminLogsMapper;
import com.horqian.api.service.sys.SysAdminLogsService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 系统 - 日志管理 服务实现类
 * </p>
 *
 * @author 孟
 * @since 2023-02-13
 */
@Service
@RequiredArgsConstructor
public class SysAdminLogsServiceImpl extends ServiceImpl<SysAdminLogsMapper, SysAdminLogs> implements SysAdminLogsService {

    private final JwtTokenUtil jwtTokenUtil;

    private final SysUserService sysUserService;

    private final String tokenHeader = "Authorization";

    private final String tokenHead = "Bearer";


    @Override
    public void saveAdminLog(HttpServletRequest request) {

        AdminLogsTypeEnum adminLogsTypeEnum = AdminLogsTypeEnum.adminLogsTypeEnum(request.getRequestURI());
        if (adminLogsTypeEnum != null) {
            // 操作日志
            SysAdminLogs sysAdminLogs = new SysAdminLogs();
            Map<String, String[]> parameterMap = request.getParameterMap();
            // 获取用户
            SysUser user = getUser(request);
            if (user.getId() != null) {
                // 用户id
                sysAdminLogs.setUserId(user.getId());
                // 用户名称
                sysAdminLogs.setUsername(user.getUsername());
                // 用户昵称
                sysAdminLogs.setNickname(user.getNickname());
                // 操作时间
                sysAdminLogs.setTime(LocalDateTime.now());
                // 日志内容
                sysAdminLogs.setContent(user.getNickname()  + " : " + adminLogsTypeEnum.title);
                // todo 日志管理系统
                // 保存用户操作日志
                this.save(sysAdminLogs);
            }
        }

    }

    /**
     * 获取用户名称
     *
     * @param request
     * @return
     */
    public SysUser getUser(HttpServletRequest request) {
        // 用户列表
        SysUser user = new SysUser();
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            //校验token
            try {
                Integer userId = JwtTokenUtil.getUserId(authToken);
                if (userId != null) {
                    user = sysUserService.getById(userId);
                }
            } catch (Exception e) {
                // 打印异常
                e.printStackTrace();
            }
        }
        return user;
    }

}
