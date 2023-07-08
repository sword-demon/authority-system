package top.wjstar.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResult {
    // 用户编号
    private Long id;
    // 状态码
    private int code;
    // token 令牌
    private String token;
    // token 过期时间
    private Long expireTime;
}
