package com.cmcorg.engine.web.tencent.properties;

import com.cmcorg.engine.web.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.TENCENT)
public class TencentProperties {

    @Schema(description = "密钥对 secretId")
    private String secretId;

    @Schema(description = "密钥对 secretKey")
    private String secretKey;

    @Schema(description = "短信应用ID")
    private String sdkAppId;

    @Schema(description = "短信签名内容")
    private String signName;

}
