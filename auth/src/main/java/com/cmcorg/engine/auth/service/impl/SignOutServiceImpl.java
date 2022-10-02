package com.cmcorg.engine.auth.service.impl;

import com.cmcorg.engine.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.auth.service.SignOutService;
import com.cmcorg.engine.auth.util.AuthUserUtil;
import com.cmcorg.engine.auth.util.MyJwtUtil;
import com.cmcorg.engine.auth.util.RequestUtil;
import com.cmcorg.engine.model.model.constant.BaseConstant;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class SignOutServiceImpl implements SignOutService {

    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    RedissonClient redissonClient;

    /**
     * 退出登录
     */
    @Override
    public String signOut() {

        String jwtStr = MyJwtUtil.getJwtStrFromHeader(httpServletRequest);

        if (jwtStr == null) {
            return BaseBizCodeEnum.OK;
        }

        Long currentUserId = AuthUserUtil.getCurrentUserId();

        String jwtHash = MyJwtUtil
            .generateRedisJwtHash(jwtStr, currentUserId, RequestUtil.getRequestCategoryEnum(httpServletRequest));

        redissonClient.getBucket(jwtHash).set("不可用的 jwt", BaseConstant.JWT_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return BaseBizCodeEnum.OK;
    }
}
