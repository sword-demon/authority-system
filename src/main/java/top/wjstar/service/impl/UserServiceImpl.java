package top.wjstar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wjstar.dao.UserMapper;
import top.wjstar.entity.User;
import top.wjstar.service.UserService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wxvirus
 * @since 2023-07-07
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findUserByUserName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        // 执行查询
        return baseMapper.selectOne(queryWrapper);
    }
}
