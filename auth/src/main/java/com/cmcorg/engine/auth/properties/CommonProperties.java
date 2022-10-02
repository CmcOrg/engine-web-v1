package com.cmcorg.engine.auth.properties;

import com.cmcorg.engine.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SYS_COMMON)
@RefreshScope
public class CommonProperties {

    @Schema(description = "平台名称")
    private String platformName = "CmcOrg";

}
