package com.cmcorg.engine.web.redisson.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Supplier;

@Component
public class RedissonUtil {

    private static final String PRE_REDISSON = "PRE_REDISSON:";

    private static RedissonClient redissonClient;

    public RedissonUtil(RedissonClient redissonClient) {
        RedissonUtil.redissonClient = redissonClient;
    }

    /**
     * 获取一般的锁，并执行方法
     */
    public static <T> T doLock(String str, Supplier<T> supplier) {

        RLock lock = redissonClient.getLock(PRE_REDISSON + str);
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取连锁，并执行方法
     */
    public static <T> T doMultiLock(String preName, Set<?> nameSet, Supplier<T> supplier, RLock... locks) {

        RLock lock = getMultiLock(preName, nameSet, locks);
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取连锁
     */
    private static RLock getMultiLock(String preName, Set<?> nameSet, RLock... locks) {

        if (preName == null) {
            preName = ""; // 防止：null 变成 "null"
        }

        RLock[] lockArr;
        if (locks == null) {
            lockArr = new RLock[nameSet.size()];
        } else {
            lockArr = new RLock[nameSet.size() + locks.length];
        }

        int i = 0;
        for (Object item : nameSet) {
            lockArr[i] = redissonClient.getLock(PRE_REDISSON + preName + item); // 设置锁名
            i++;
        }

        if (locks != null) {
            for (RLock item : locks) {
                lockArr[i] = item;
                i++;
            }
        }

        return redissonClient.getMultiLock(lockArr);
    }

}
