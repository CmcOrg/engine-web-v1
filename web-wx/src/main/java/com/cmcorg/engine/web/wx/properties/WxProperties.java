package com.cmcorg.engine.web.wx.properties;

import com.cmcorg.engine.web.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.WX)
public class WxProperties {

    @Schema(description = "appId")
    private String appId;

    @Schema(description = "secret")
    private String secret;

}
