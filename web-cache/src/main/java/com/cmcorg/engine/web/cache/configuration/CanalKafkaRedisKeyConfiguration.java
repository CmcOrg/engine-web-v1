package com.cmcorg.engine.web.cache.configuration;

import com.cmcorg.engine.web.cache.model.enums.CanalKafkaHandlerKeyEnum;
import com.cmcorg.engine.web.cache.util.CanalKafkaHandlerUtil;
import org.springframework.stereotype.Component;

@Component
public class CanalKafkaRedisKeyConfiguration {

    public CanalKafkaRedisKeyConfiguration(CanalKafkaHandlerUtil canalKafkaHandlerUtil) {
        canalKafkaHandlerUtil.putCanalKafkaHandlerMap(CanalKafkaHandlerKeyEnum.values());
    }

}
