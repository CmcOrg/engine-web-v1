package com.cmcorg.engine.web.redisson.enums;

/**
 * redis中 key的枚举类
 * 备注：如果是 redisson的锁 key，一定要备注：锁什么，例如：锁【用户主键 id】
 * 备注：【PRE_】开头，表示 key后面还要跟字符串
 * 备注：【_CACHE】结尾，表示 key后面不用跟字符串
 */
public enum RedisKeyEnum {

    // 【PRE_】开头 ↓
    PRE_IP_BLACK, // ip黑名单前端，后面跟 ip
    PRE_IP_TOTAL_CHECK, // ip 请求总数，key前缀，后面跟 ip

    PRE_JWT_HASH, // jwtHash 前缀

    PRE_EMAIL, // 邮箱：锁【邮箱】
    PRE_SIGN_IN_NAME, // 登录名：锁【登录名】

    PRE_TOO_MANY_PASSWORD_ERRORS, // 密码错误次数太多：锁【用户主键 id】
    PRE_PASSWORD_ERROR_COUNT, // 密码错误总数：锁【用户主键 id】

    PRE_NETTY_TCP_PROTO_BUF_CONNECT_SECURITY_CODE, // netty tcp protoBuf 连接时的身份认证 code前缀

    // 【_CACHE】结尾 ↓
    USER_ID_JWT_SECRET_SUF_CACHE, // 用户 id和 jwt私钥后缀

    USER_ID_REF_ROLE_ID_SET_CACHE, // 用户 id关联的 roleIdSet
    DEFAULT_ROLE_ID_CACHE, // 默认角色 id
    ROLE_REF_MENU_ID_SET_CACHE, // 角色 id关联的 menuIdSet
    ALL_MENU_ID_AND_AUTHS_LIST_CACHE, // menu集合，包含所有菜单：id和 auths

    SYS_PARAM_CACHE, // 系统参数缓存

    // 其他 ↓
    ATOMIC_LONG_ID_GENERATOR, // 获取主键 id，自增值

    ;

}
