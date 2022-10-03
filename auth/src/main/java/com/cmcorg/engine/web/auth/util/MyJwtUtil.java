package com.cmcorg.engine.web.auth.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.mapper.SysUserMapper;
import com.cmcorg.engine.web.auth.model.constant.AuthConstant;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.auth.model.entity.SysMenuDO;
import com.cmcorg.engine.web.auth.model.entity.SysUserDO;
import com.cmcorg.engine.web.auth.model.enums.RequestCategoryEnum;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.properties.AuthProperties;
import com.cmcorg.engine.web.cache.util.MyCacheUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MyJwtUtil {

    // 系统里的 jwt密钥
    private static final String JWT_SECRET_SYS =
        "4282dde8cb54c0c68082ada1b1d9ce048195cd3090e07d1ed3e1871b462a8b75fee46467b96f33dea6511862f1ea4867aed76243dfe7e1efb89638d3da6570d1";

    public static final String PAYLOAD_MAP_USER_ID_KEY = "userId";

    private static AuthProperties authProperties;
    private static SysUserMapper sysUserMapper;

    public MyJwtUtil(AuthProperties authProperties, SysUserMapper sysUserMapper) {
        MyJwtUtil.authProperties = authProperties;
        MyJwtUtil.sysUserMapper = sysUserMapper;
    }

    /**
     * 统一生成 jwt
     */
    @Nullable
    public static String generateJwt(Long userId, String jwtSecretSuf) {

        if (userId == null) {
            return null;
        }

        if (BaseConstant.ADMIN_ID.equals(userId) && !MyJwtUtil.authProperties.getAdminEnable()) {
            return null;
        }

        if (StrUtil.isBlank(jwtSecretSuf)) {
            jwtSecretSuf = MyJwtUtil.getUserJwtSecretSufByUserId(userId);
        }

        if (!BaseConstant.ADMIN_ID.equals(userId) && StrUtil.isBlank(jwtSecretSuf)) {
            return null;
        }

        return MyJwtUtil.sign(userId, jwtSecretSuf);
    }

    /**
     * 生成 jwt
     */
    @NotNull
    private static String sign(Long userId, String jwtSecretSuf) {

        JSONObject payloadMap = JSONUtil.createObj().set(PAYLOAD_MAP_USER_ID_KEY, userId);

        String jwt = JWT.create() //
            .setExpiresAt(new Date(System.currentTimeMillis() + BaseConstant.JWT_EXPIRE_TIME)) // 设置过期时间
            .addPayloads(payloadMap) // 增加JWT载荷信息
            .setKey(MyJwtUtil.getJwtSecret(jwtSecretSuf).getBytes()) // 设置密钥
            .sign();

        return AuthConstant.JWT_PREFIX + jwt;
    }

    /**
     * 生成 redis中，jwt存储使用的 key（jwtHash），目的：不直接暴露明文的 jwt
     */
    @NotNull
    public static String generateRedisJwtHash(String jwtStr, Long userId, RequestCategoryEnum requestCategoryEnum) {

        StrBuilder strBuilder = StrBuilder.create(RedisKeyEnum.PRE_JWT_HASH.name());
        strBuilder.append(":").append(userId).append(":").append(requestCategoryEnum.getCode()).append(":")
            .append(DigestUtil.sha512Hex(jwtStr));

        return strBuilder.toString();
    }

    /**
     * 获取 jwt密钥：配置的私钥前缀 + JWT_SECRET_SYS + 用户的私钥后缀
     * 备注：admin的 jwtSecretSuf 就是 "null"
     */
    @NotNull
    public static String getJwtSecret(String jwtSecretSuf) {
        return MyJwtUtil.authProperties.getJwtSecretPre() + MyJwtUtil.JWT_SECRET_SYS + jwtSecretSuf;
    }

    /**
     * 从请求头里，获取：jwt字符串
     */
    @Nullable
    public static String getJwtStrFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader(AuthConstant.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(AuthConstant.JWT_PREFIX)) {
            return null;
        }
        String jwtStr = authorization.replace(AuthConstant.JWT_PREFIX, "");
        if (StrUtil.isBlank(jwtStr)) {
            return null;
        }
        return jwtStr;
    }

    /**
     * 获取用户 jwt私钥后缀，通过 userId
     */
    @Nullable
    public static String getUserJwtSecretSufByUserId(Long userId) {

        if (userId == null || BaseConstant.ADMIN_ID.equals(userId)) {
            return null;
        }

        Map<Long, String> map = MyCacheUtil
            .getMapCache(RedisKeyEnum.USER_ID_JWT_SECRET_SUF_CACHE, MyCacheUtil.getDefaultLongStringResultMap(), () -> {
                List<SysUserDO> sysUserDOList =
                    ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEnableFlag, true)
                        .select(BaseEntity::getId, SysUserDO::getJwtSecretSuf).list();

                return sysUserDOList.stream().collect(Collectors.toMap(BaseEntity::getId, SysUserDO::getJwtSecretSuf));
            });

        return map.get(userId);
    }

    /**
     * 通过 userId获取到权限的 set
     */
    @Nullable
    public static List<SimpleGrantedAuthority> getSimpleGrantedAuthorityListByUserId(Long userId) {

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST); // 直接抛出异常
            return null;
        }

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            userId = 1L;
        }

        // 通过用户 id，获取 菜单集合
        List<SysMenuDO> sysMenuDOList = AuthUserUtil.getMenuListByUserId(userId, 2);

        if (CollUtil.isEmpty(sysMenuDOList)) {
            return null;
        }

        Set<String> authsSet = sysMenuDOList.stream().map(SysMenuDO::getAuths).collect(Collectors.toSet());

        // 组装权限，并去重
        Set<String> authSet = new HashSet<>();
        for (String item : authsSet) {
            if (StrUtil.isBlank(item)) {
                continue;
            }
            List<String> splitList = StrUtil.split(item, ",");
            for (String auth : splitList) {
                if (StrUtil.isNotBlank(auth)) {
                    authSet.add(auth);
                }
            }
        }

        return authSet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
