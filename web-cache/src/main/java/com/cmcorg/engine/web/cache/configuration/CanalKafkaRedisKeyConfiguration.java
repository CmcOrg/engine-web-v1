package com.cmcorg.engine.web.cache.configuration;

import cn.hutool.extra.spring.SpringUtil;
import com.cmcorg.engine.web.cache.model.enums.CanalKafkaHandlerKeyEnum;
import org.springframework.stereotype.Component;

@Component
public class CanalKafkaRedisKeyConfiguration {

    public CanalKafkaRedisKeyConfiguration() {
        for (CanalKafkaHandlerKeyEnum item : CanalKafkaHandlerKeyEnum.values()) {
            SpringUtil.registerBean(item.getClass().getName() + "." + item.getName(), item);
        }
    }

}
