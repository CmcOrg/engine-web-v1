package com.cmcorg.engine.web.cache.listener;

import cn.hutool.cache.Cache;
import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.web.cache.model.interfaces.ICanalKafkaHandlerKey;
import com.cmcorg.engine.web.cache.util.CanalKafkaHandlerUtil;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.redisson.model.interfaces.IRedisKey;
import com.cmcorg.engine.web.util.util.TypeReferenceUtil;
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
    "#{T(com.cmcorg.engine.web.kafka.enums.KafkaTopicEnum).LOCAL_CACHE_TOPIC.name()}"}, containerFactory = "dynamicGroupIdContainerFactory", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE)
public class LocalCacheKafkaListener {

    @Resource
    Cache<Enum<? extends IRedisKey>, Object> cache;

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        Set<Enum<? extends IRedisKey>> redisKeyEnumSet =
            recordList.stream().map(it -> JSONUtil.toBean(it, TypeReferenceUtil.STRING_SET, false))
                .flatMap(Collection::stream).distinct().map(CanalKafkaHandlerUtil::getByKey).filter(Objects::nonNull)
                .map(ICanalKafkaHandlerKey::getDeleteRedisKeyEnumSet).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toSet());

        if (redisKeyEnumSet.size() != 0) {
            log.info("canal：清除 本地缓存：{}", redisKeyEnumSet);
            for (Enum<? extends IRedisKey> item : redisKeyEnumSet) {
                cache.remove(item); // 清除本地缓存
            }
        }

        acknowledgment.acknowledge();
    }

}
