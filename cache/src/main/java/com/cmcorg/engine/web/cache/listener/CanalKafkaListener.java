package com.cmcorg.engine.web.cache.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.web.cache.handler.InterfaceCanalKafkaHandler;
import com.cmcorg.engine.web.cache.model.dto.CanalKafkaDTO;
import com.cmcorg.engine.web.cache.model.enums.CanalKafkaHandlerKeyEnum;
import com.cmcorg.engine.web.kafka.enums.KafkaTopicEnum;
import com.cmcorg.engine.web.kafka.util.KafkaUtil;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@KafkaListener(topics = {
    "#{T(com.cmcorg.engine.kafka.enums.KafkaTopicEnum).CANAL_TOPIC.name()}"}, groupId = "#{T(com.cmcorg.engine.kafka.enums.KafkaTopicEnum).CANAL_TOPIC.name()}", batch = "true")
@Slf4j(topic = LogTopicConstant.CACHE)
public class CanalKafkaListener {

    @Resource
    RedissonClient redissonClient;

    private final Map<String, List<InterfaceCanalKafkaHandler>> canalKafkaHandlerMap = new HashMap<>();

    /**
     * 构造器：给 canalKafkaHandlerMap 添加元素
     */
    public CanalKafkaListener(List<InterfaceCanalKafkaHandler> interfaceCanalKafkaHandlerList) {

        for (CanalKafkaHandlerKeyEnum item : CanalKafkaHandlerKeyEnum.values()) {
            if (CollUtil.isEmpty(item.getDeleteRedisKeyEnumSet())) {
                continue;
            }
            // 添加一个 InterfaceCanalKafkaHandler，进行删除操作
            for (RedisKeyEnum subItem : item.getDeleteRedisKeyEnumSet()) {
                putCanalKafkaHandlerMap(item, new InterfaceCanalKafkaHandler() {
                    @Override
                    public Set<CanalKafkaHandlerKeyEnum> getKeySet() {
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
        }

        if (CollUtil.isEmpty(interfaceCanalKafkaHandlerList)) {
            return;
        }

        for (InterfaceCanalKafkaHandler item : interfaceCanalKafkaHandlerList) {
            if (CollUtil.isEmpty(item.getKeySet())) {
                continue;
            }
            for (CanalKafkaHandlerKeyEnum subItem : item.getKeySet()) {
                putCanalKafkaHandlerMap(subItem, item);
            }
        }

    }

    /**
     * 给 canalKafkaHandlerMap 添加元素
     */
    private void putCanalKafkaHandlerMap(CanalKafkaHandlerKeyEnum canalKafkaHandlerKeyEnum,
        InterfaceCanalKafkaHandler canalKafkaHandler) {

        List<InterfaceCanalKafkaHandler> handlerList =
            canalKafkaHandlerMap.getOrDefault(canalKafkaHandlerKeyEnum.getKey(), CollUtil.newArrayList());

        handlerList.add(canalKafkaHandler);

        canalKafkaHandlerMap.put(canalKafkaHandlerKeyEnum.getKey(), handlerList);
    }

    @KafkaHandler
    public void receive(List<String> recordList, Acknowledgment acknowledgment) {

        if (canalKafkaHandlerMap.size() == 0) {
            acknowledgment.acknowledge();
            return;
        }

        RBatch batch = redissonClient.createBatch();

        Set<String> keySet = new HashSet<>();

        for (String item : recordList) {

            CanalKafkaDTO canalKafkaDTO = JSONUtil.toBean(item, CanalKafkaDTO.class);

            String key = canalKafkaDTO.getDatabase() + "." + canalKafkaDTO.getTable();

            List<InterfaceCanalKafkaHandler> handlerList = canalKafkaHandlerMap.get(key);

            if (CollUtil.isNotEmpty(handlerList)) {
                keySet.add(key);
                for (InterfaceCanalKafkaHandler subItem : handlerList) {
                    subItem.handler(canalKafkaDTO, batch); // 处理
                }
            }

        }

        batch.execute(); // 执行 batch

        if (keySet.size() != 0) {
            log.info("canal：清除 redis缓存：CanalKafkaHandlerKeyEnum中的，keySet：{}", keySet);
            KafkaUtil.send(KafkaTopicEnum.LOCAL_CACHE_TOPIC, keySet); // 发送：本地缓存处理的 topic
        }

        acknowledgment.acknowledge();
    }

}

