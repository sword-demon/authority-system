package top.wjstar.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存储 token 信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVo {
    // 过期时间
    private Long expireTime;
    // token
    private String token;
}
