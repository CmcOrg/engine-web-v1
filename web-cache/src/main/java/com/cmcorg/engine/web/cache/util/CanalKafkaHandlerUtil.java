package com.cmcorg.engine.web.cache.util;

import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.web.cache.model.interfaces.ICanalKafkaHandlerKey;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CanalKafkaHandlerUtil {

    private static List<ICanalKafkaHandlerKey> iCanalKafkaHandlerKeyList;

    public CanalKafkaHandlerUtil(List<ICanalKafkaHandlerKey> iCanalKafkaHandlerKeyList) {
        CanalKafkaHandlerUtil.iCanalKafkaHandlerKeyList = iCanalKafkaHandlerKeyList;
    }

    @Nullable
    public static ICanalKafkaHandlerKey getByKey(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        for (ICanalKafkaHandlerKey item : iCanalKafkaHandlerKeyList) {
            if (key.equals(item.getKey())) {
                return item;
            }
        }
        return null;
    }

}
