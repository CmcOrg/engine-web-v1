package com.cmcorg.engine.web.netty.boot.configuration;

import cn.hutool.json.JSONObject;

public interface INettyPrincipalJsonConfiguration {

    /**
     * 给 principalJson添加：额外的属性
     */
    void handler(final JSONObject principalJson);

}
