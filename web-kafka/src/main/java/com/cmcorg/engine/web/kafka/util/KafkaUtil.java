package com.cmcorg.engine.web.kafka.util;

import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.web.kafka.enums.KafkaTopicEnum;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 工具类
 */
@Component
public class KafkaUtil {

    private static KafkaTemplate<String, String> kafkaTemplate;

    public KafkaUtil(KafkaTemplate<String, String> kafkaTemplate) {
        KafkaUtil.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送消息
     */
    public static <T> void send(KafkaTopicEnum kafkaTopicEnum, T data) {
        kafkaTemplate.send(kafkaTopicEnum.name(), JSONUtil.toJsonStr(data));
    }

}
