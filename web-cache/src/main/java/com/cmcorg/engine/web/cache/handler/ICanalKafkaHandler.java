package com.cmcorg.engine.web.cache.handler;

import com.cmcorg.engine.web.cache.model.dto.CanalKafkaDTO;
import com.cmcorg.engine.web.cache.model.interfaces.ICanalKafkaHandlerKey;
import org.redisson.api.RBatch;

import java.util.Set;

public interface ICanalKafkaHandler {

    /**
     * 类似：cmcorg.sys_menu 格式
     */
    Set<ICanalKafkaHandlerKey> getKeySet();

    /**
     * 如果 keySet 包含，则进行处理
     */
    void handler(final CanalKafkaDTO dto, final RBatch batch);

}
