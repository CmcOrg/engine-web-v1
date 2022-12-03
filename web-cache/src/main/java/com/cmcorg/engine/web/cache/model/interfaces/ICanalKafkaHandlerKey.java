package com.cmcorg.engine.web.cache.model.interfaces;

import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.web.cache.listener.CanalKafkaListener;
import com.cmcorg.engine.web.redisson.model.interfaces.IRedisKey;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ICanalKafkaHandlerKey {

    /**
     * 备注：会截取后半段，判断是否是数字，例如：cmcorg.sys_user_0，则会变成：cmcorg.sys_user
     */
    String getName();

    /**
     * 要移除的 redisKeySet，会默认添加 ICanalKafkaHandler，进行删除
     */
    Set<Enum<? extends IRedisKey>> getDeleteRedisKeyEnumSet();

    @Nullable
    default String getKey() {
        if (StrUtil.isBlank(getName())) {
            return null;
        }
        return CanalKafkaListener.cacheProperties.getDatabaseName() + "." + getName();
    }

}
