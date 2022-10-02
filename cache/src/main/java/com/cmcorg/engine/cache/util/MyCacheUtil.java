package com.cmcorg.engine.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cmcorg.engine.model.model.constant.BaseConstant;
import com.cmcorg.engine.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.redisson.enums.RedisKeyEnum;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 缓存工具类
 */
@Component
@Slf4j(topic = LogTopicConstant.CACHE)
public class MyCacheUtil {

    private static Cache<RedisKeyEnum, Object> cache;
    private static RedissonClient redissonClient;

    public MyCacheUtil(Cache<RedisKeyEnum, Object> cache, RedissonClient redissonClient) {
        MyCacheUtil.cache = cache;
        MyCacheUtil.redissonClient = redissonClient;
    }

    @NotNull
    public static Map<Long, Set<Long>> getDefaultLongSetLongResultMap() {
        Map<Long, Set<Long>> defaultResultMap = MapUtil.newHashMap();
        defaultResultMap.put(BaseConstant.SYS_ID, new HashSet<>());
        return defaultResultMap;
    }

    @NotNull
    public static Map<Long, String> getDefaultLongStringResultMap() {
        Map<Long, String> defaultResultMap = MapUtil.newHashMap();
        defaultResultMap.put(BaseConstant.SYS_ID, null);
        return defaultResultMap;
    }

    @NotNull
    public static <T> List<T> getDefaultResultList() {
        List<T> defaultResultList = CollUtil.newArrayList();
        defaultResultList.add(null);
        return defaultResultList;
    }

    @NotNull
    private static <T> T checkAndSetResultToDefault(T result, T defaultResult) {
        if (defaultResult == null) {
            throw new RuntimeException("操作失败：defaultResult == null"); // 不能为 null，目的：防止缓存不写入数据
        }
        if (result == null) {
            log.info("MyCacheUtil：设置默认值：{}", defaultResult);
            result = defaultResult;
        } else if (result instanceof Map && CollUtil.isEmpty((Map)result)) {
            log.info("MyCacheUtil：设置默认值：{}", defaultResult);
            result = defaultResult;
        }
        return result;
    }

    /**
     * 获取：一般类型的缓存
     */
    @NotNull
    public static <T> T getCache(RedisKeyEnum redisKeyEnum, T defaultResult, @NotNull Supplier<T> supplier) {

        T result = (T)cache.getIfPresent(redisKeyEnum);

        if (result != null) {
            log.info("{}：返回 本地缓存", redisKeyEnum.name());
            return result;
        }

        result = (T)redissonClient.getBucket(redisKeyEnum.name()).get();

        if (result == null) {
            log.info("{}：读取数据库数据", redisKeyEnum.name());
            result = supplier.get();
        } else {
            log.info("{}：加入 本地缓存，并返回 redis缓存", redisKeyEnum.name());
            cache.put(redisKeyEnum, result);
            return result;
        }

        result = checkAndSetResultToDefault(result, defaultResult);// 检查并设置为默认值

        log.info("{}：加入 redis缓存", redisKeyEnum.name());
        redissonClient.getBucket(redisKeyEnum.name()).setAsync(result); // 优先保证 redis里面成功写入

        log.info("{}：加入 本地缓存", redisKeyEnum.name());
        cache.put(redisKeyEnum, result);

        return result;
    }

    /**
     * 获取：map类型的缓存
     */
    @NotNull
    public static <T extends Map<?, ?>> T getMapCache(RedisKeyEnum redisKeyEnum, T defaultResultMap,
        @NotNull Supplier<T> supplier) {

        T resultMap = (T)cache.getIfPresent(redisKeyEnum);

        if (CollUtil.isNotEmpty(resultMap)) {
            log.info("{}：返回 本地缓存", redisKeyEnum.name());
            return resultMap;
        }

        if (redissonClient.getMap(redisKeyEnum.name()).isExists()) {
            resultMap = (T)redissonClient.getMap(redisKeyEnum.name()).readAllMap();
            if (CollUtil.isNotEmpty(resultMap)) {
                log.info("{}：加入 本地缓存，并返回 redis缓存", redisKeyEnum.name());
                cache.put(redisKeyEnum, resultMap);
                return resultMap;
            }
        }

        log.info("{}：读取数据库数据", redisKeyEnum.name());
        resultMap = supplier.get();

        resultMap = checkAndSetResultToDefault(resultMap, defaultResultMap); // 检查并设置为默认值

        log.info("{}：加入 redis缓存", redisKeyEnum.name());
        RBatch batch = redissonClient.createBatch(); // 优先保证 redis里面成功写入
        batch.getMap(redisKeyEnum.name()).deleteAsync();
        batch.getMap(redisKeyEnum.name()).putAllAsync(resultMap);
        batch.execute();

        log.info("{}：加入 本地缓存", redisKeyEnum.name());
        cache.put(redisKeyEnum, resultMap);

        return resultMap;
    }

    /**
     * 获取：list类型的缓存
     */
    @NotNull
    public static <T extends List<?>> T getListCache(RedisKeyEnum redisKeyEnum, T defaultResultList,
        @NotNull Supplier<T> supplier) {

        T resultList = (T)cache.getIfPresent(redisKeyEnum);

        if (CollUtil.isNotEmpty(resultList)) {
            log.info("{}：返回 本地缓存", redisKeyEnum.name());
            return resultList;
        }

        resultList = (T)redissonClient.getList(redisKeyEnum.name()).readAll();

        if (CollUtil.isNotEmpty(resultList)) {
            log.info("{}：加入 本地缓存，并返回 redis缓存", redisKeyEnum.name());
            cache.put(redisKeyEnum, resultList);
            return resultList;
        }

        log.info("{}：读取数据库数据", redisKeyEnum.name());
        resultList = supplier.get();

        resultList = checkAndSetResultToDefault(resultList, defaultResultList); // 检查并设置为默认值

        log.info("{}：加入 redis缓存", redisKeyEnum.name());
        RBatch batch = redissonClient.createBatch(); // 优先保证 redis里面成功写入
        batch.getList(redisKeyEnum.name()).deleteAsync();
        batch.getList(redisKeyEnum.name()).addAllAsync(resultList);
        batch.execute();

        log.info("{}：加入 本地缓存", redisKeyEnum.name());
        cache.put(redisKeyEnum, resultList);

        return resultList;
    }

}
