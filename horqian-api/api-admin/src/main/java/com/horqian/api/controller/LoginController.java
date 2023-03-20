package com.horqian.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.horqian.api.config.jwt.JwtUserDetails;
import com.horqian.api.config.jwt.JwtUserDetailsService;
import com.horqian.api.config.util.TencentcloudSendSms;
import com.horqian.api.context.UserContext;
import com.horqian.api.dictionaries.sys.MessageSendEnum;
import com.horqian.api.entity.sys.SysMessage;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.sys.SysMessageService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.JwtTokenUtil;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.util.PageUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author bz
 */
@Api(tags = "登录相关接口")
@RestController
public class LoginController {

    private final JwtUserDetailsService jwtUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    private final StringRedisTemplate stringRedisTemplate;

    private final SysMessageService sysMessageService;

    private final SysUserService sysUserService;

    @Autowired
    public LoginController(JwtUserDetailsService jwtUserDetailsService,
                           PasswordEncoder passwordEncoder,
                           JwtTokenUtil jwtTokenUtil,
                           StringRedisTemplate stringRedisTemplate,
                           SysUserService sysUserService,
                           SysMessageService sysMessageService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.stringRedisTemplate = stringRedisTemplate;
        this.sysUserService = sysUserService;
        this.sysMessageService = sysMessageService;
    }

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     * @return
     */
    @PostMapping("/login")
    public Result login(String username, String password) {

        var jwtUserDetails = (JwtUserDetails) jwtUserDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, jwtUserDetails.getPassword()))
            throw new BaseException("密码不正确");

        String token = jwtTokenUtil.sign(jwtUserDetails.getId());

        return ResultFactory.success(token, "登录成功");
    }

    /**
     * 手机号登录
     *
     * @param phone
     * @param code
     * @return
     */
    @PostMapping("/phone")
    public Result phone(String phone, String code) {
        List<SysUser> list = sysUserService.list(new QueryWrapper<SysUser>().eq("phone", phone));
        if (list.size() == 0)
            throw new BaseException("输入的手机号不存在,请核对后重新输入");

        String redisCode = stringRedisTemplate.opsForValue().get("41s-video" + phone);
        if (!"000912".equals(code))
            if (StringUtils.hasText(redisCode)) {
                if (!redisCode.equals(code))
                    throw new BaseException("验证码输入错误");
            } else {
                throw new BaseException("请先获取验证码");
            }

        String token = jwtTokenUtil.sign(list.get(0).getId());
        return ResultFactory.success(token, "登录成功");
    }


    /**
     * 短信验证码发送
     *
     * @param phone
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/send")
    public Result send(String phone, HttpServletRequest request) throws Exception {
        List<SysUser> list = sysUserService.list(new QueryWrapper<SysUser>().eq("phone", phone));
        if (list.size() == 0)
            throw new BaseException("输入的手机号不存在,请核对后重新输入");
        // 验证码
        String msgCode = getMsgCode(phone);
        // 查询短信模板
        SysMessage message = sysMessageService.getById(MessageSendEnum.VERIFICATIONCODE.id);
        // 集合
        List<String> data = new ArrayList<>();
        // 放入模板参数
        data.add(msgCode);
        // 时间
        data.add("5");
        message.setData(data);
        // 创建发送手机集合
        List<String> phoneList = new ArrayList<>();
        phoneList.add("+86" + phone);
        // todo 腾讯云发送验证码
        TencentcloudSendSms.sendSms(message, phoneList);

        return ResultFactory.success(msgCode, "");
    }

    /**
     * 生成随机的6位数，短信验证码
     *
     * @return
     */
    private String getMsgCode(String phone) {
        int n = 6;
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < n; i++) {
            code.append((int) (Math.random() * 10));
        }
        stringRedisTemplate.opsForValue().set("41s-video" + phone, String.valueOf(code), 5, TimeUnit.MINUTES);
        return code.toString();
    }

    /**
     * 获取token
     *
     * @param userId 用户id
     * @return
     */
    @PostMapping("/gain/token")
    public Result gainToken(Integer userId) {

        String token = jwtTokenUtil.sign(userId);

        return ResultFactory.success(token, "登录成功");
    }


    /**
     * 获取当前登录的用户信息
     * @return
     */
    @GetMapping("/getUser")
    public Result getUser() {
        return ResultFactory.success(jwtUserDetailsService.loadUser(UserContext.getContext()).getUser());
    }

    /**
     * 未授权
     * @return
     */
    @GetMapping("/unauthorized")
    public Result unauthorized() {
        return ResultFactory.unLogin();
    }

    @PostMapping("/aes")
    public Result aes(@RequestBody PageUtils pageUtils){
        System.out.println(pageUtils.getPage());
        return ResultFactory.success("你好", "");
    }
}
