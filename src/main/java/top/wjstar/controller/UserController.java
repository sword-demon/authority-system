package top.wjstar.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.wjstar.service.UserService;
import top.wjstar.utils.Result;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wxvirus
 * @since 2023-07-07
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 所有用户信息
     *
     * @return
     */
    @GetMapping("/listAll")
    public Result listAll() {
        return Result.ok(userService.list());
    }
}

