package top.wjstar.config.security.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import top.wjstar.utils.Result;
import top.wjstar.utils.ResultCode;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 登录认证失败处理器
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        // 设置响应的编码格式
        response.setContentType("application/json;charset=utf-8");

        // 定义变量保存异常的信息
        String message;
        // 判断异常类型
        // 账户过期
        if (exception instanceof AccountExpiredException) {
            message = "账户过期，登录失败";
        } else if (exception instanceof BadCredentialsException) {
            message = "用户名密码错误，登录失败";
        } else if (exception instanceof CredentialsExpiredException) {
            message = "密码过期,登录失败";
        } else if (exception instanceof DisabledException) {
            message = "账户被禁用，登录失败";
        } else if (exception instanceof LockedException) {
            message = "账户被锁，登录失败";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            message = "账户不存在，登录失败";
        } else {
            message = "登录失败";
        }

        // 将结果转换成 JSON 格式
        String result = JSON.toJSONString(Result.error().code(ResultCode.ERROR).message(message));

        // 将结果保存到输出流中
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
