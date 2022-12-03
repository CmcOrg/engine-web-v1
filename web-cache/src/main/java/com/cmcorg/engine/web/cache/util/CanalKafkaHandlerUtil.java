package com.cmcorg.engine.web.cache.util;

import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.web.cache.handler.ICanalKafkaHandler;
import com.cmcorg.engine.web.cache.listener.CanalKafkaListener;
import com.cmcorg.engine.web.cache.model.dto.CanalKafkaDTO;
import com.cmcorg.engine.web.cache.model.interfaces.ICanalKafkaHandlerKey;
import com.cmcorg.engine.web.redisson.model.interfaces.IRedisKey;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBatch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CanalKafkaHandlerUtil {

    private final static List<ICanalKafkaHandlerKey> I_CANAL_KAFKA_HANDLER_KEY_LIST = new ArrayList<>();

    @Nullable
    public static ICanalKafkaHandlerKey getByKey(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        for (ICanalKafkaHandlerKey item : I_CANAL_KAFKA_HANDLER_KEY_LIST) {
            if (key.equals(item.getKey())) {
                return item;
            }
        }
        return null;
    }

    public static void putCanalKafkaHandlerMap(ICanalKafkaHandlerKey[] iCanalKafkaHandlerKeyArr) {

        for (ICanalKafkaHandlerKey item : iCanalKafkaHandlerKeyArr) {
            // 添加一个 ICanalKafkaHandler，进行删除操作
            for (Enum<? extends IRedisKey> subItem : item.getDeleteRedisKeyEnumSet()) {
                CanalKafkaListener.putCanalKafkaHandlerMap(item, new ICanalKafkaHandler() {
                    @Override
                    public Set<ICanalKafkaHandlerKey> getKeySet() {
                        return null;
                    }

                    @Override
                    public void handler(CanalKafkaDTO dto, RBatch batch) {
                        if (dto.getType().dateUpdateFlag()) {
                            batch.getBucket(subItem.name()).deleteAsync();
                        }
                    }
                });
            }

            I_CANAL_KAFKA_HANDLER_KEY_LIST.add(item); // 添加到集合里面：暂时用于：CanalKafkaHandlerUtil#getByKey

        }

    }

}
