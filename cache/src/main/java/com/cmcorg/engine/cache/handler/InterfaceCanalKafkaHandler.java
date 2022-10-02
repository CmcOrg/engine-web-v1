package com.cmcorg.engine.cache.handler;

import com.cmcorg.engine.cache.model.dto.CanalKafkaDTO;
import com.cmcorg.engine.cache.model.enums.CanalKafkaHandlerKeyEnum;
import org.redisson.api.RBatch;

import java.util.Set;

public interface InterfaceCanalKafkaHandler {

    /**
     * 类似：game_project.sys_menu 格式
     */
    Set<CanalKafkaHandlerKeyEnum> getKeySet();

    /**
     * 如果 keySet 包含，则进行处理
     */
    void handler(final CanalKafkaDTO dto, final RBatch batch);

}
