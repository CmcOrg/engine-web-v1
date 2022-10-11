package com.cmcorg.engine.web.model.model.constant;

public interface LogTopicConstant {

    String PRE_SYS = "sys.";

    String CACHE = PRE_SYS + "cache"; // 缓存相关

    String JAVA_TO_WEB = PRE_SYS + "javaToWeb"; // java直接生成web页面

    String NETTY = PRE_SYS + "netty"; // netty相关

    String USER = PRE_SYS + "user"; // user相关

    String PRE_GAME = "game.";

    String ROOM_CURRENT = PRE_GAME + "roomCurrent"; // 当前房间相关

}
