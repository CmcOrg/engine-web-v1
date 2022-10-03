package com.cmcorg.engine.web.auth.configuration.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.cmcorg.engine.web.auth.filter.JwtAuthorizationFilter;
import com.cmcorg.engine.web.auth.properties.AuthProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启 @PreAuthorize 权限注解
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    @SneakyThrows
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
        @Value("${spring.profiles.active:prod}") String profiles,
        List<InterfaceAuthPermitAllConfiguration> interfaceAuthPermitAllConfigurationList,
        RedissonClient redissonClient, AuthProperties authProperties) {

        boolean prodFlag = "prod".equals(profiles);

        Set<String> permitAllSet = new HashSet<>();

        if (CollUtil.isNotEmpty(interfaceAuthPermitAllConfigurationList)) {
            for (InterfaceAuthPermitAllConfiguration item : interfaceAuthPermitAllConfigurationList) {
                if (prodFlag) {
                    CollUtil.addAll(permitAllSet, item.prodPermitAllSet());
                } else {
                    CollUtil.addAll(permitAllSet, item.devPermitAllSet());
                }
                CollUtil.addAll(permitAllSet, item.anyPermitAllSet());
            }
        }

        log.info("permitAllSet：{}", permitAllSet);

        httpSecurity.authorizeRequests().antMatchers(ArrayUtil.toArray(permitAllSet, String.class))
            .permitAll() // 可以匿名访问的请求
            .anyRequest().authenticated(); // 拦截所有请求

        httpSecurity.addFilterBefore(new JwtAuthorizationFilter(redissonClient, authProperties),
            UsernamePasswordAuthenticationFilter.class);

        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 不需要session

        // 用户没有登录，但是访问需要权限的资源时，而报出的错误
        httpSecurity.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint());

        httpSecurity.csrf().disable(); // 关闭CSRF保护

        httpSecurity.logout().disable(); // 禁用 logout

        httpSecurity.formLogin().disable(); // 禁用 login

        return httpSecurity.build();
    }

}
