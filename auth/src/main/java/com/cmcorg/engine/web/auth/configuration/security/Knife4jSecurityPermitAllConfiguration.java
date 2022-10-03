package com.cmcorg.engine.web.auth.configuration.security;

import cn.hutool.core.collection.CollUtil;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class Knife4jSecurityPermitAllConfiguration implements InterfaceAuthPermitAllConfiguration {

    @Override
    public Set<String> devPermitAllSet() {
        return CollUtil.newHashSet("/v3/api-docs/**", "/webjars/**", "/doc.html/**");
    }

    @Override
    public Set<String> prodPermitAllSet() {
        return null;
    }

    @Override
    public Set<String> anyPermitAllSet() {
        return null;
    }

}
