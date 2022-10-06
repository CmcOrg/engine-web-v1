package com.cmcorg.engine.web.cache.properties;

import com.cmcorg.engine.web.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SYS_CACHE)
public class CacheProperties {

    @Schema(description = "数据库名称")
    private String databaseName = "service_engine_web_v1";

}
