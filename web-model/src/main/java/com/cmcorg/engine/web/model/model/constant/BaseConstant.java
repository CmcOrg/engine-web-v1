package com.cmcorg.engine.web.model.model.constant;

/**
 * 通用的常量类
 */
public interface BaseConstant {

    String NEGATIVE_ONE_STR = "-1";

    // 过期时间相关 ↓
    long DAY_1_EXPIRE_TIME = 60 * 60 * 1000 * 24; // 1天过期
    long DAY_7_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 7L; // 7天过期
    long DAY_15_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 15L; // 15天过期
    long DAY_30_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 30L; // 30天过期
    long HOUR_3_EXPIRE_TIME = 60 * 60 * 1000 * 3; // 3小时过期
    int MINUTE_30_EXPIRE_TIME = 30 * 60 * 1000; // 30分钟过期
    int MINUTE_10_EXPIRE_TIME = 10 * 60 * 1000; // 10分钟过期，这个一般用于长一点的验证码的过期时间
    int MINUTE_1_EXPIRE_TIME = 60 * 1000; // 1分钟过期
    int SECOND_1_EXPIRE_TIME = 1000; // 1秒钟过期
    int SECOND_2_EXPIRE_TIME = 2000; // 2秒钟过期
    int SECOND_3_EXPIRE_TIME = 3 * 1000; // 3秒钟过期
    int SECOND_6_EXPIRE_TIME = 6 * 1000; // 6秒钟过期
    int SECOND_10_EXPIRE_TIME = 10 * 1000; // 10秒钟过期
    int SECOND_20_EXPIRE_TIME = 20 * 1000; // 20秒钟过期，这个一般用于短暂验证码的过期时间
    int SECOND_30_EXPIRE_TIME = 30 * 1000; // 30秒钟过期

    long JWT_EXPIRE_TIME = DAY_1_EXPIRE_TIME; // jwt 过期时间
    long LONG_CODE_EXPIRE_TIME = MINUTE_10_EXPIRE_TIME; // 长一点的验证码的过期时间
    long SHORT_CODE_EXPIRE_TIME = SECOND_20_EXPIRE_TIME; // 短暂验证码的过期时间
    // 过期时间相关 ↑

    // id 相关 ↓
    Long ADMIN_ID = 0L; // 管理员 id
    String ADMIN_ACCOUNT = "admin"; // 管理员 登录名，如果修改，请注意【登录名】注册，是否会有影响
    Long SYS_ID = -1L; // 系统/缺省 id，或者表示不存在
    // id 相关 ↑

}
