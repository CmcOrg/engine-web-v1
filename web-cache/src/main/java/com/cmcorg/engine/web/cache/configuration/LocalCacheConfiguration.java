package com.cmcorg.engine.web.cache.configuration;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class LocalCacheConfiguration {

    @Bean
    public Cache<RedisKeyEnum, Object> cache() {
        return Caffeine.newBuilder().build();
    }

    /**
     * 结论：hutoolCache，会快一倍左右
     */
    public static void main(String[] args) {

        Cache<String, String> caffeineCache = Caffeine.newBuilder().maximumSize(1000).build();

        FIFOCache<String, String> hutoolCache = CacheUtil.newFIFOCache(1000);

        HashMap<String, String> hashMap = MapUtil.newHashMap(20000);

        for (int i = 0; i < 20000; i++) {
            String simpleUUID = IdUtil.simpleUUID();
            hashMap.put(simpleUUID, simpleUUID);
        }

        List<String> keyList = new ArrayList<>(hashMap.keySet());

        TimeInterval timer = DateUtil.timer();

        for (int i = 0; i < 500; i++) {

            for (Map.Entry<String, String> item : hashMap.entrySet()) {
                hutoolCache.put(item.getKey(), item.getValue());
            }

            for (int j = 0; j < 10; j++) {
                hutoolCache.get(RandomUtil.randomEle(keyList));
                hutoolCache.remove(RandomUtil.randomEle(keyList));
            }

            hutoolCache.clear();
        }

        long interval2 = timer.intervalRestart();
        log.info("hutoolCache：执行结束 =====================> 耗时：{}毫秒", interval2);

        for (int i = 0; i < 500; i++) {

            for (Map.Entry<String, String> item : hashMap.entrySet()) {
                caffeineCache.put(item.getKey(), item.getValue());
            }

            for (int j = 0; j < 10; j++) {
                caffeineCache.getIfPresent(RandomUtil.randomEle(keyList));
                caffeineCache.invalidate(RandomUtil.randomEle(keyList));
            }

            caffeineCache.invalidateAll();
        }

        long interval = timer.intervalRestart();
        log.info("caffeineCache：执行结束 =====================> 耗时：{}毫秒", interval);

    }

}
