package top.wjstar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.wjstar.entity.User;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wxvirus
 * @since 2023-07-07
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName
     * @return
     */
    User findUserByUserName(String userName);
}
