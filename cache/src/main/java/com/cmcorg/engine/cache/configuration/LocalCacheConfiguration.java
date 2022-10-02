package com.cmcorg.engine.cache.configuration;

import com.cmcorg.engine.redisson.enums.RedisKeyEnum;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalCacheConfiguration {

    @Bean
    public Cache<RedisKeyEnum, Object> cache() {
        return Caffeine.newBuilder().build();
    }

}
