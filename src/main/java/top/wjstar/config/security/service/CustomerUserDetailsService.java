package top.wjstar.config.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import top.wjstar.entity.User;
import top.wjstar.service.UserService;

import javax.annotation.Resource;

@Component
public class CustomerUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户信息
        User user = userService.findUserByUserName(username);
        // 如果对象为空，认证失败
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 查询成功返回用户对象
        return user;
    }
}
