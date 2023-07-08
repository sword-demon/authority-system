package top.wjstar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.wjstar.entity.Permission;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wxvirus
 * @since 2023-07-07
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 根据用户 id 查询权限菜单列表
     *
     * @param userId 用户 id
     * @return 权限菜单列表
     */
    List<Permission> findPermissionListByUserId(Long userId);
}
