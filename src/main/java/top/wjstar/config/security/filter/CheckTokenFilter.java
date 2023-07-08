package top.wjstar.config.security.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import top.wjstar.config.redis.RedisService;
import top.wjstar.config.security.exception.CustomerAuthenticationException;
import top.wjstar.config.security.handler.LoginFailureHandler;
import top.wjstar.config.security.service.CustomerUserDetailsService;
import top.wjstar.utils.JwtUtils;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CheckTokenFilter extends OncePerRequestFilter {

    /**
     * 登录请求地址
     */
    @Value("${request.login.url}")
    private String loginUrl;

    @Resource
    private RedisService redisService;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private CustomerUserDetailsService customerUserDetailsService;

    @Resource
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 获取当钱请求的 url 地址
            String uri = request.getRequestURI();
            // 判断当前请求是否是登录请求，如果不是登录请求，则需要验证 token
            if (!uri.equals(loginUrl)) {
                // 进行 token 的认证
                this.validateToken(request);
            }
        } catch (AuthenticationException e) {
            // 验证失败
            loginFailureHandler.onAuthenticationFailure(request, response, e);
        }

        // 登录请求不需要携带 token 直接放行
        doFilter(request, response, filterChain);
    }

    /**
     * 验证 token 信息
     *
     * @param request
     */
    private void validateToken(HttpServletRequest request) {
        // 获取前端提交的 token 进行
        // 先从 headers 头部获取 token 信息
        String token = request.getHeader("token");
        // 如果请求头部没有携带 token，则从请求的参数中获取 token
        if (ObjectUtils.isEmpty(token)) {
            // 从参数中获取
            token = request.getParameter("token");
        }
        // 如果请求参数中也没有携带 token 信息则抛出异常
        if (ObjectUtils.isEmpty(token)) {
            throw new CustomerAuthenticationException("token不存在");
        }
        // 判断 redis 中是否存在 token
        String tokenKey = "token_" + token;
        // 从 redis 里获取 token
        String redisToken = redisService.get(tokenKey);
        // 判断 redis 中是否存在 token 信息，如果为空，则表示 token 已经失效
        if (ObjectUtils.isEmpty(redisToken)) {
            throw new CustomerAuthenticationException("token认证失败");
        }

        // 如果存在 token ，如果和 redis 里的 token 不一致，也是验证失败
        if (!token.equals(redisToken)) {
            throw new CustomerAuthenticationException("token认证失败");
        }

        // 如果 token 存在，从 token 中解析出用户信息
        String username = jwtUtils.getUsernameFromToken(token);
        if (ObjectUtils.isEmpty(username)) {
            throw new CustomerAuthenticationException("token解析失败");
        }
        // 获取用户信息
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new CustomerAuthenticationException("token 解析失败");
        }

        // 创建用户身份认证对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 设置请求的信息
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 将认证的信息交给 spring security 上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
