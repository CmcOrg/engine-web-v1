package com.cmcorg.engine.web.log.properties;

import com.cmcorg.engine.web.model.model.constant.PropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SYS_LOG)
@RefreshScope
@Component
public class LogProperties {

    /**
     * 为空时，则是正常的日志状态，不为空时，则只打印集合里面日志
     */
    Set<String> logTopicSet;

}
