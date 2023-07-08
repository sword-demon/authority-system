package top.wjstar.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.wjstar.entity.Permission;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wxvirus
 * @since 2023-07-07
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    // 根据用户 id 查询用户的权限菜单列表
    List<Permission> findPermissionListByUserId(Long userId);
}
