package top.wjstar.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.wjstar.config.security.filter.CheckTokenFilter;
import top.wjstar.config.security.handler.AnonymousAuthenticationHandler;
import top.wjstar.config.security.handler.CustomerAccessDeniedHandler;
import top.wjstar.config.security.handler.LoginFailureHandler;
import top.wjstar.config.security.handler.LoginSuccessHandler;
import top.wjstar.config.security.service.CustomerUserDetailsService;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private LoginSuccessHandler loginSuccessHandler;
    @Resource
    private LoginFailureHandler loginFailureHandler;

    @Resource
    private AnonymousAuthenticationHandler anonymousAuthenticationHandler;
    @Resource
    private CustomerAccessDeniedHandler customerAccessDeniedHandler;

    @Resource
    private CustomerUserDetailsService customerUserDetailsService;

    @Resource
    private CheckTokenFilter checkTokenFilter;

    /**
     * 注入加密类
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 处理登录认证
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 登录前进行过滤
        http.addFilterBefore(checkTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 如果表单请求的用户名不是 username 和 password 还需要单独设置
        // 登录过程的处理
        http.formLogin()    // 表单登录
                .loginProcessingUrl("/api/user/login") // 设置登录请求的 url 地址，自定义
                .successHandler(loginSuccessHandler) // 登录认证成功的处理器
                .failureHandler(loginFailureHandler) // 认证失败得处理器
                .and()
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 不创建 session
                .and()
                .authorizeRequests() // 设置需要拦截的请求
                .antMatchers("/api/user/login").permitAll() // 登录请求放行
                .anyRequest().authenticated() // 其他一律请求都需要身份认证拦截
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(anonymousAuthenticationHandler) // 匿名无权限访问
                .accessDeniedHandler(customerAccessDeniedHandler) // 认证用户无权限访问
                .and()
                .cors(); // 支持跨域请求
    }

    /**
     * 配置认证处理器
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customerUserDetailsService).passwordEncoder(passwordEncoder());
    }
}
