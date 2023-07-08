package top.wjstar.utils;

import org.springframework.beans.BeanUtils;
import top.wjstar.entity.Permission;
import top.wjstar.vo.RouterVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 生成菜单树
 */
public class MenuTree {

    /**
     * 生成路由
     *
     * @param menuList 菜单列表
     * @param pid      父菜单的 id
     * @return 返回路由信息
     */
    public static List<RouterVo> makeRouter(List<Permission> menuList, Long pid) {
        // 创建集合保存路由信息
        List<RouterVo> routerVoList = new ArrayList<>();
        // 判断当前菜单列表是否为空,如果不为空，则使用菜单列表，否则创建集合对象，防止空指针
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                // 筛选不为空的菜单及菜单父 id 相同的数据
                .stream().filter(item -> item != null && Objects.equals(item.getParentId(), pid))
                .forEach(item -> {
                    // 创建路由对象
                    RouterVo routerVo = new RouterVo();
                    routerVo.setName(item.getName());
                    routerVo.setPath(item.getPath());
                    // 判断是否是一级菜单
                    if (item.getParentId() == 0L) {
                        // 一级菜单默认组件 Layout
                        routerVo.setComponent("Layout");
                        routerVo.setAlwaysShow(true); // 显示路由
                    } else {
                        routerVo.setComponent(item.getUrl()); // 具体的某一个组件
                        routerVo.setAlwaysShow(false); // 表示折叠路由菜单
                    }
                    // 设置 meta 信息
                    routerVo.setMeta(routerVo.new Meta(item.getLabel(), item.getIcon(), item.getCode().split(",")));
                    // 递归生成路由
                    // 子菜单
                    List<RouterVo> childrenRouteVo = makeRouter(menuList, item.getId());
                    routerVo.setChildren(childrenRouteVo); // 设置子路由到路由对象中
                    // 将路由信息添加到集合中
                    routerVoList.add(routerVo);
                });

        // 返回路由信息
        return routerVoList;
    }

    /**
     * 生成菜单树
     *
     * @param menuList 菜单列表
     * @param pid      父菜单 id
     * @return 返回菜单列表
     */
    public static List<Permission> makeMenuTree(List<Permission> menuList, Long pid) {
        // 创建集合保存对象
        List<Permission> permissionList = new ArrayList<>();
        // 判断当前菜单列表是否为空,如果不为空，则使用菜单列表，否则创建集合对象，防止空指针
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream().filter(item -> item != null && item.getParentId() == pid)
                .forEach(item -> {
                    // 创建权限菜单
                    Permission permission = new Permission();
                    // 将原有的属性复制给菜单对象
                    BeanUtils.copyProperties(item, permission);
                    // 获取每一个 item 对象的子菜单递归生成菜单树
                    List<Permission> childrenPermission = makeMenuTree(menuList, item.getId());
                    permission.setChildren(childrenPermission);
                    // 将菜单对象添加到集合
                    permissionList.add(permission);
                });
        return permissionList;
    }
}
