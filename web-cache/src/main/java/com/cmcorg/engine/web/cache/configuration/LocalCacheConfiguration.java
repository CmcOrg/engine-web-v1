package com.cmcorg.engine.web.cache.configuration;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LocalCacheConfiguration {

    @Bean
    public Cache<RedisKeyEnum, Object> cache() {
        return CacheUtil.newLRUCache(5120);
    }

}
