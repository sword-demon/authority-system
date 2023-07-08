package top.wjstar.config.security.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import top.wjstar.entity.Permission;
import top.wjstar.entity.User;
import top.wjstar.service.PermissionService;
import top.wjstar.service.UserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CustomerUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户信息
        User user = userService.findUserByUserName(username);
        // 如果对象为空，认证失败
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 查询用户的拥有的权限列表
        List<Permission> permissionList = permissionService.findPermissionListByUserId(user.getId());

        // 获取对应的权限编码 code 字段
        List<String> codeList = permissionList.stream().filter(Objects::nonNull)
                .map(Permission::getCode)
                .filter(Objects::nonNull) // 过滤不为空的
                .collect(Collectors.toList());

        // 将权限编码列表转换为一个数组
        String[] strings = codeList.toArray(new String[codeList.size()]);
        // 设置权限列表
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(strings);
        // 将权限设置给用户对象
        user.setAuthorities(authorityList);
        // 设置该用户拥有的菜单信息
        user.setPermissionList(permissionList);
        // 查询成功返回用户对象
        return user;
    }
}
