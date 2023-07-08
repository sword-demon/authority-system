package top.wjstar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录用户信息类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    // 用户 id
    private Long id;
    // 用户名称
    private String name;
    // 头像
    private String avatar;
    // 介绍
    private String introduction;
    // 角色权限集合
    private Object[] roles;
}
