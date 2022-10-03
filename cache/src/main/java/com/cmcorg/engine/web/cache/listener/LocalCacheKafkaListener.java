package com.cmcorg.engine.web.cache.listener;

import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.web.cache.model.enums.CanalKafkaHandlerKeyEnum;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import com.cmcorg.engine.web.util.util.TypeReferenceUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@KafkaListener(topics = {
    "#{T(com.cmcorg.engine.kafka.enums.KafkaTopicEnum).LOCAL_CACHE_TOPIC.name()}"}, containerFactory = "dynamicGroupIdContainerFactory", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE)
public class LocalCacheKafkaListener {

    @Resource
    Cache<RedisKeyEnum, Object> cache;

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        Set<RedisKeyEnum> redisKeyEnumSet =
            recordList.stream().map(it -> JSONUtil.toBean(it, TypeReferenceUtil.STRING_SET, true))
                .flatMap(Collection::stream).distinct().map(CanalKafkaHandlerKeyEnum::getByKey).filter(Objects::nonNull)
                .map(CanalKafkaHandlerKeyEnum::getDeleteRedisKeyEnumSet).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toSet());

        if (redisKeyEnumSet.size() != 0) {
            log.info("canal：清除 本地缓存：{}", redisKeyEnumSet);
            cache.invalidateAll(redisKeyEnumSet); // 清除本地缓存
        }

        acknowledgment.acknowledge();
    }

}
