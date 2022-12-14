package com.cmcorg.engine.web.auth.properties;

import com.cmcorg.engine.web.model.model.constant.PropertiesPrefixConstant;
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

    @Schema(description = "外网：ip/域名")
    private String internetAddress = "127.0.0.1";

}
