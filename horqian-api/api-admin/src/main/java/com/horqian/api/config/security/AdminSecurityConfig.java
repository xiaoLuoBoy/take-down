package com.horqian.api.config.security;


import com.horqian.api.config.jwt.RestAuthenticationEntryPoint;
import com.horqian.api.config.jwt.RestfulAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author bz
 */
@Configuration
@EnableWebSecurity
public class AdminSecurityConfig extends WebSecurityConfig {

    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 禁用缓存
        httpSecurity.headers().cacheControl();

        //http相关的配置，包括登入登出、异常处理、会话管理等
        httpSecurity.authorizeRequests()
                .antMatchers("/v2/api-docs",//swagger api json
                        "/swagger-resources/configuration/ui",//用来获取支持的动作
                        "/swagger-resources",//用来获取api-docs的URI
                        "/swagger-resources/configuration/security",//安全选项
                        "/swagger-ui.html",
                        "/doc.html",
                        "/webjars/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/**").permitAll()
//                .antMatchers("/sys/user/list").access("hasAuthority('get_user')")
                .antMatchers("/files/**").permitAll()
                //系统管理
                .antMatchers("/sys/user/list").access("hasAuthority('user_show')")
                .antMatchers("/sys/user/save").access("hasAuthority('user_save')")
                .antMatchers("/sys/user/delete").access("hasAuthority('user_delete')")
                .antMatchers("/sys/user/password").access("hasAuthority('user_password')")

                .anyRequest().authenticated()
                .and()
                .logout().permitAll();

        httpSecurity.addFilterBefore(adminJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        httpSecurity.exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);

    }


    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        //获取用户账号密码及权限信息
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AdminJwtTokenFilter adminJwtTokenFilter() {
        return new AdminJwtTokenFilter();
    }
}
