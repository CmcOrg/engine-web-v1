package com.cmcorg.engine.web.auth.configuration.security;

import com.cmcorg.engine.web.auth.util.AuthUserUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * 自定义的 投票者
 */
public class MyAccessDecisionVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

        // 如果是 admin，则可以访问所有接口
        if (BaseConstant.ADMIN_ID.equals(AuthUserUtil.getCurrentUserId())) {
            return ACCESS_GRANTED; // 同意票
        }

        return ACCESS_ABSTAIN; // 中立票
    }

}
