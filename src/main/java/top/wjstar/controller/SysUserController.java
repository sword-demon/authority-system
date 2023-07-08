package top.wjstar.controller;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.wjstar.config.redis.RedisService;
import top.wjstar.entity.Permission;
import top.wjstar.entity.User;
import top.wjstar.entity.UserInfo;
import top.wjstar.utils.JwtUtils;
import top.wjstar.utils.Result;
import top.wjstar.vo.TokenVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisService redisService;

    /**
     * 刷新 token
     *
     * @param request
     * @return
     */
    @PostMapping("/refreshToken")
    public Result refreshToken(HttpServletRequest request) {
        // 从 headers 里获取 token 信息
        String token = request.getHeader("token");
        // 判断 headers 头部是否存在 token 信息
        if (ObjectUtils.isEmpty(token)) {
            // 从请求参数中获取 token
            token = request.getParameter("token");
        }
        // 从 spring security 上下文中获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取用户的身份信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 生成新的 token 信息
        String refreshToken = "";
        // 验证提交过来的 token 信息是否是合法的
        if (jwtUtils.validateToken(token, userDetails)) {
            // 重新生成新的 token
            refreshToken = jwtUtils.refreshToken(token);
        }
        // 获取本次 token 的到期时间
        long expireTime = Jwts.parser().setSigningKey(jwtUtils.getSecret())
                .parseClaimsJws(refreshToken.replace("jwt_", ""))
                .getBody().getExpiration().getTime();
        // 清除原来的 token 信息
        String oldTokenKey = "token_" + token;
        redisService.del(oldTokenKey);
        // 将新的 token 信息保存到 redis 缓存中
        String newTokenKey = "token_" + refreshToken;
        redisService.set(newTokenKey, refreshToken, jwtUtils.getExpiration() / 1000);
        // 创建 tokenvo 对象
        TokenVo tokenVo = new TokenVo(expireTime, refreshToken);
        return Result.ok(tokenVo).message("token 刷新成功");
    }

    /**
     * 获取用户信息
     *
     * @return Result
     */
    @GetMapping("/getInfo")
    public Result getInfo() {
        // 从 spring security 上下文获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 判断用户信息是否为空
        if (authentication == null) {
            return Result.error().message("用户信息查询失败");
        }
        // 获取用户信息
        User user = (User) authentication.getPrincipal();
        // 获取该用户拥有的角色权限信息
        List<Permission> permissionList = user.getPermissionList();
        // 获取权限编码
        Object[] roles = permissionList.stream().filter(Objects::nonNull)
                .map(Permission::getCode).toArray();

        // 创建用户返回信息
        UserInfo userInfo = new UserInfo(user.getId(), user.getRealName(), user.getAvatar(), null, roles);
        return Result.ok(userInfo).message("用户信息查询成功");
    }
}
