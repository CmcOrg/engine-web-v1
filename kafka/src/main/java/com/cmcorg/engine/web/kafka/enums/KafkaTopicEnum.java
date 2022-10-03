package com.cmcorg.engine.web.kafka.enums;

import cn.hutool.core.lang.TypeReference;
import com.cmcorg.engine.web.util.util.TypeReferenceUtil;
import lombok.AllArgsConstructor;

/**
 * kafka 主题枚举类
 */
@AllArgsConstructor
public enum KafkaTopicEnum {

    CANAL_TOPIC(null, null), // canal
    LOCAL_CACHE_TOPIC(null, TypeReferenceUtil.STRING_SET), // 本地缓存 topic

    ;

    private Class<?> clazz;
    private TypeReference<?> typeReference;

}
