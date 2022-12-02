package com.cmcorg.engine.web.auth.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.mapper.*;
import com.cmcorg.engine.web.auth.model.entity.*;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.cache.util.MyCacheUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import com.cmcorg.engine.web.redisson.model.enums.RedisKeyEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthUserUtil {

    private static SysMenuMapper sysMenuMapper;
    private static SysRoleMapper sysRoleMapper;
    private static SysRoleRefMenuMapper sysRoleRefMenuMapper;
    private static SysRoleRefUserMapper sysRoleRefUserMapper;
    private static SysUserMapper sysUserMapper;

    public AuthUserUtil(SysMenuMapper sysMenuMapper, SysRoleMapper sysRoleMapper,
        SysRoleRefMenuMapper sysRoleRefMenuMapper, SysRoleRefUserMapper sysRoleRefUserMapper,
        SysUserMapper sysUserMapper) {
        AuthUserUtil.sysMenuMapper = sysMenuMapper;
        AuthUserUtil.sysRoleMapper = sysRoleMapper;
        AuthUserUtil.sysRoleRefMenuMapper = sysRoleRefMenuMapper;
        AuthUserUtil.sysRoleRefUserMapper = sysRoleRefUserMapper;
        AuthUserUtil.sysUserMapper = sysUserMapper;
    }

    /**
     * 获取当前 userId
     * 这里只会返回实际的 userId，如果为 null，则会抛出异常
     */
    @NotNull
    public static Long getCurrentUserId() {

        Long userId = getCurrentUserIdWillNull();

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.NOT_LOGGED_IN_YET);
        }

        return userId;
    }

    /**
     * 获取当前用户的 邮箱，如果是 admin账号，则会报错，只会返回当前用户的 邮箱，不会返回 null
     */
    @NotNull
    public static String getCurrentUserEmailNotAdmin() {

        Long currentUserIdNotAdmin = getCurrentUserIdNotAdmin();

        SysUserDO sysUserDO = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, currentUserIdNotAdmin)
            .select(SysUserDO::getEmail).one();

        if (sysUserDO == null || StrUtil.isBlank(sysUserDO.getEmail())) {
            ApiResultVO.error(BaseBizCodeEnum.UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_EMAIL_ADDRESS_IS_NOT_BOUND);
        }

        return sysUserDO.getEmail();
    }

    /**
     * 获取当前用户的 手机号码，如果是 admin账号，则会报错，只会返回当前用户的 手机号码，不会返回 null
     */
    @NotNull
    public static String getCurrentUserPhoneNotAdmin() {

        Long currentUserIdNotAdmin = getCurrentUserIdNotAdmin();

        SysUserDO sysUserDO = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, currentUserIdNotAdmin)
            .select(SysUserDO::getPhone).one();

        if (sysUserDO == null || StrUtil.isBlank(sysUserDO.getPhone())) {
            ApiResultVO.error(BaseBizCodeEnum.UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_EMAIL_ADDRESS_IS_NOT_BOUND);
        }

        return sysUserDO.getEmail();
    }

    /**
     * 获取当前 userId，如果是 admin账号，则会报错，只会返回 用户id，不会返回 null
     * 因为 admin不支持一些操作，例如：修改密码，修改邮箱等
     */
    @NotNull
    public static Long getCurrentUserIdNotAdmin() {

        Long currentUserId = getCurrentUserId();

        if (BaseConstant.ADMIN_ID.equals(currentUserId)) {
            ApiResultVO.error(BaseBizCodeEnum.THE_ADMIN_ACCOUNT_DOES_NOT_SUPPORT_THIS_OPERATION);
        }

        return currentUserId;
    }

    /**
     * 这里只会返回实际的 userId 或者 -1，备注：-1表示没有 用户id，在大多数情况下，表示的是 系统
     */
    @NotNull
    public static Long getCurrentUserIdDefault() {

        Long userId = getCurrentUserIdWillNull();

        if (userId == null) {
            userId = BaseConstant.SYS_ID;
        }

        return userId;
    }

    /**
     * 获取当前 userId，注意：这里获取 userId之后需要做 非空判断
     * 这里只会返回实际的 userId或者 null
     */
    @Nullable
    private static Long getCurrentUserIdWillNull() {
        return Convert.toLong(getSecurityContextHolderContextAuthenticationPrincipalJsonObjectValueByKey(
            MyJwtUtil.PAYLOAD_MAP_USER_ID_KEY));
    }

    /**
     * 获取：当前 security上下文里面存储的用户信息，通过：key
     */
    @Nullable
    public static <T> T getSecurityContextHolderContextAuthenticationPrincipalJsonObjectValueByKey(String key) {

        T result = null;

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principalObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principalObject instanceof JSONObject) {
                result = (T)((JSONObject)principalObject).get(key);
            }
        }

        return result;

    }

    /**
     * 通过用户 id，获取 菜单集合
     * type：1 完整的菜单信息 2 给 security获取权限时使用
     */
    @Nullable
    public static List<SysMenuDO> getMenuListByUserId(Long userId, int type) {

        Set<Long> roleIdSet = new HashSet<>();

        // 获取用户关联的角色
        getUserRefRoleIdSet(userId, roleIdSet);

        // 获取默认角色 id
        getDefaultRoleId(roleIdSet);

        if (roleIdSet.size() == 0) {
            return null;
        }

        // 获取角色关联的菜单
        Set<Long> menuIdSet = getRoleRefMenuIdSet(roleIdSet);

        if (CollUtil.isEmpty(menuIdSet)) {
            return null;
        }

        // 获取所有菜单
        List<SysMenuDO> allSysMenuDOList;
        if (type == 1) {
            allSysMenuDOList = ChainWrappers.lambdaQueryChain(sysMenuMapper)
                .select(BaseEntity::getId, BaseEntityTree::getParentId, SysMenuDO::getPath, SysMenuDO::getIcon,
                    SysMenuDO::getRouter, SysMenuDO::getName, SysMenuDO::getFirstFlag, SysMenuDO::getLinkFlag,
                    SysMenuDO::getShowFlag, SysMenuDO::getAuths, SysMenuDO::getAuthFlag, BaseEntityTree::getOrderNo,
                    SysMenuDO::getRedirect).eq(BaseEntity::getEnableFlag, true).orderByDesc(BaseEntityTree::getOrderNo)
                .list();
        } else {
            allSysMenuDOList = getAllMenuIdAndAuthsList();
        }

        if (CollUtil.isEmpty(allSysMenuDOList)) {
            return null;
        }

        // 开始进行匹配，组装返回值
        List<SysMenuDO> deepSysMenuDOList =
            allSysMenuDOList.stream().filter(it -> menuIdSet.contains(it.getId())).collect(Collectors.toList());

        if (deepSysMenuDOList.size() == 0) {
            return null;
        }

        // 根据底级节点 list，逆向生成整棵树 list
        deepSysMenuDOList = MyTreeUtil.getFullTreeList(deepSysMenuDOList, allSysMenuDOList);

        // 已经添加了 menuIdSet
        Set<Long> addMenuIdSet = deepSysMenuDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        // 所有的菜单 parentIdSet
        Set<Long> allMenuParentIdSet =
            allSysMenuDOList.stream().map(BaseEntityTree::getParentId).collect(Collectors.toSet());

        for (Long item : menuIdSet) { // 再添加 menuIdSet的所有子级菜单
            getMenuListByUserIdNext(deepSysMenuDOList, allSysMenuDOList, item, addMenuIdSet, allMenuParentIdSet);
        }

        return deepSysMenuDOList;
    }

    /**
     * 再添加 menuIdSet的所有子级菜单
     */
    private static void getMenuListByUserIdNext(List<SysMenuDO> deepSysMenuDOList, List<SysMenuDO> allSysMenuDOList,
        Long parentId, Set<Long> addMenuIdSet, Set<Long> allMenuParentIdSet) {

        if (!allMenuParentIdSet.contains(parentId)) {
            return;
        }

        for (SysMenuDO item : allSysMenuDOList) {
            if (item.getParentId().equals(parentId)) {
                if (!addMenuIdSet.contains(item.getId())) { // 不能重复添加到 返回值里
                    addMenuIdSet.add(item.getId());
                    deepSysMenuDOList.add(item);
                }
                getMenuListByUserIdNext(deepSysMenuDOList, allSysMenuDOList, item.getId(), addMenuIdSet,
                    allMenuParentIdSet); // 继续匹配下一级
            }
        }

    }

    /**
     * 获取所有菜单
     */
    @Nullable
    private static List<SysMenuDO> getAllMenuIdAndAuthsList() {
        List<SysMenuDO> sysMenuDOList = MyCacheUtil
            .getListCache(RedisKeyEnum.ALL_MENU_ID_AND_AUTHS_LIST_CACHE, MyCacheUtil.getDefaultResultList(),
                () -> ChainWrappers.lambdaQueryChain(sysMenuMapper)
                    .select(BaseEntity::getId, BaseEntityTree::getParentId, SysMenuDO::getAuths)
                    .eq(BaseEntity::getEnableFlag, true).list());

        if (sysMenuDOList.size() == 1 && sysMenuDOList.get(0) == null) {
            return null;
        } else {
            return sysMenuDOList;
        }
    }

    /**
     * 获取角色关联的菜单
     */
    @NotNull
    private static Set<Long> getRoleRefMenuIdSet(Set<Long> roleIdSet) {
        Map<Long, Set<Long>> roleRefMenuIdSetMap = MyCacheUtil
            .getMapCache(RedisKeyEnum.ROLE_REF_MENU_ID_SET_CACHE, MyCacheUtil.getDefaultLongSetLongResultMap(), () -> {
                List<SysRoleRefMenuDO> sysRoleRefMenuDOList = ChainWrappers.lambdaQueryChain(sysRoleRefMenuMapper)
                    .select(SysRoleRefMenuDO::getRoleId, SysRoleRefMenuDO::getMenuId).list();

                return sysRoleRefMenuDOList.stream().collect(Collectors.groupingBy(SysRoleRefMenuDO::getRoleId,
                    Collectors.mapping(SysRoleRefMenuDO::getMenuId, Collectors.toSet())));
            });

        return roleRefMenuIdSetMap.entrySet().stream().filter(it -> roleIdSet.contains(it.getKey()))
            .flatMap(it -> it.getValue().stream()).collect(Collectors.toSet());
    }

    /**
     * 获取用户关联的角色
     */
    private static void getUserRefRoleIdSet(Long userId, Set<Long> roleIdSet) {
        Map<Long, Set<Long>> userRefRoleIdSetMap = MyCacheUtil
            .getMapCache(RedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE, MyCacheUtil.getDefaultLongSetLongResultMap(),
                () -> {
                    List<SysRoleRefUserDO> sysRoleRefUserDOList = ChainWrappers.lambdaQueryChain(sysRoleRefUserMapper)
                        .select(SysRoleRefUserDO::getRoleId, SysRoleRefUserDO::getUserId).list();

                    return sysRoleRefUserDOList.stream().collect(Collectors.groupingBy(SysRoleRefUserDO::getUserId,
                        Collectors.mapping(SysRoleRefUserDO::getRoleId, Collectors.toSet())));
                });

        Set<Long> userRefRoleIdSet = userRefRoleIdSetMap.get(userId);

        if (CollUtil.isNotEmpty(userRefRoleIdSet)) {
            roleIdSet.addAll(userRefRoleIdSet);
        }
    }

    /**
     * 获取默认角色 id
     */
    private static void getDefaultRoleId(Set<Long> roleIdSet) {
        Long defaultRoleId = MyCacheUtil.getCache(RedisKeyEnum.DEFAULT_ROLE_ID_CACHE, BaseConstant.SYS_ID, () -> {
            SysRoleDO sysRoleDO = ChainWrappers.lambdaQueryChain(sysRoleMapper).eq(SysRoleDO::getDefaultFlag, true)
                .eq(BaseEntity::getEnableFlag, true).select(BaseEntity::getId).one();

            if (sysRoleDO != null) {
                return sysRoleDO.getId();
            }
            return null;
        });

        if (!BaseConstant.SYS_ID.equals(defaultRoleId)) {
            roleIdSet.add(defaultRoleId);
        }
    }

}
