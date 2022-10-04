package com.cmcorg.engine.web.auth.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.properties.AuthProperties;
import com.cmcorg.engine.web.auth.util.MyJwtUtil;
import com.cmcorg.engine.web.auth.util.RequestUtil;
import com.cmcorg.engine.web.auth.util.ResponseUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 自定义 jwt过滤器，备注：后续接口方法，无需判断账号是否封禁或者不存在
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static RedissonClient redissonClient;
    private static AuthProperties authProperties;

    public JwtAuthorizationFilter(RedissonClient redissonClient, AuthProperties authProperties) {
        JwtAuthorizationFilter.redissonClient = redissonClient;
        JwtAuthorizationFilter.authProperties = authProperties;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(request, response);

        if (usernamePasswordAuthenticationToken != null) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    @SneakyThrows
    @Nullable
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
        HttpServletResponse response) {

        String jwtStr = MyJwtUtil.getJwtStrFromHeader(request);

        if (jwtStr == null) {
            return null;
        }

        JWT jwt;
        try {
            jwt = JWT.of(jwtStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Long userId = Convert.toLong(jwt.getPayload(MyJwtUtil.PAYLOAD_MAP_USER_ID_KEY));
        if (userId == null) {
            return null;
        }

        String jwtHash = MyJwtUtil.generateRedisJwtHash(jwtStr, userId, RequestUtil.getRequestCategoryEnum(request));

        // 判断 jwtHash是否存在于 redis中，如果存在，则表示不能使用
        boolean hasKey = redissonClient.getBucket(jwtHash).isExists();
        if (hasKey) {
            return loginExpired(response); // 提示登录过期，请重新登录
        }

        String jwtSecretSuf = null;
        if (BaseConstant.ADMIN_ID.equals(userId)) {
            if (!authProperties.getAdminEnable()) {
                return null;
            }
        } else {
            // 如果不是 admin
            jwtSecretSuf = MyJwtUtil.getUserJwtSecretSufByUserId(userId);  // 通过 userId获取到 私钥后缀
            if (StrUtil.isBlank(jwtSecretSuf)) { // 除了 admin账号，每个账号都肯定有 jwtSecretSuf
                return null;
            }
        }

        jwt.setKey(MyJwtUtil.getJwtSecret(jwtSecretSuf).getBytes());

        // 验证算法
        if (!jwt.verify()) {
            return loginExpired(response); // 提示登录过期，请重新登录，目的：为了可以随时修改配置的 jwt前缀，或者用户 jwt后缀修改
        }

        try {
            // 校验时间字段：如果过期了，这里会抛出 ValidateException异常
            JWTValidator.of(jwt).validateDate(new Date());
        } catch (ValidateException e) {
            return loginExpired(response); // 提示登录过期，请重新登录
        }

        // 通过 userId 获取用户具有的权限
        return new UsernamePasswordAuthenticationToken(userId, null,
            MyJwtUtil.getSimpleGrantedAuthorityListByUserId(userId));
    }

    /**
     * 提示登录过期，请重新登录
     */
    @Nullable
    private UsernamePasswordAuthenticationToken loginExpired(HttpServletResponse response) {
        ResponseUtil.out(response, BaseBizCodeEnum.LOGIN_EXPIRED);
        return null;
    }

}
