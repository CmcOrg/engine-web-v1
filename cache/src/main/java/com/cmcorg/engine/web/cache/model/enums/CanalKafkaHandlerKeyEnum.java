package com.cmcorg.engine.web.cache.model.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "canal的消息枚举")
public enum CanalKafkaHandlerKeyEnum {

    game_project_sys_menu("game_project.sys_menu", CollUtil
        .newHashSet(RedisKeyEnum.ALL_MENU_ID_AND_AUTHS_LIST_CACHE, RedisKeyEnum.ROLE_REF_MENU_ID_SET_CACHE)), // 菜单表

    game_project_sys_user("game_project.sys_user", CollUtil
        .newHashSet(RedisKeyEnum.USER_ID_JWT_SECRET_SUF_CACHE, RedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE)), // 用户表

    game_project_sys_role("game_project.sys_role", CollUtil
        .newHashSet(RedisKeyEnum.DEFAULT_ROLE_ID_CACHE, RedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE,
            RedisKeyEnum.ROLE_REF_MENU_ID_SET_CACHE)), // 角色表

    ;

    private String name; // 备注：会截取后半段，判断是否是数字，例如：game_project.sys_user_0，则会变成：game_project.sys_user
    private Set<RedisKeyEnum> deleteRedisKeyEnumSet; // 要移除的 redisKeySet，会默认添加 ICanalKafkaHandler，进行删除

    CanalKafkaHandlerKeyEnum(String name) {
        this.name = name;
    }

    @NotNull
    public String getKey() {
        if (StrUtil.isBlank(getName())) {
            return this.name(); // 获取枚举类的名称
        }
        return getName();
    }

    @Nullable
    public static CanalKafkaHandlerKeyEnum getByKey(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        for (CanalKafkaHandlerKeyEnum item : CanalKafkaHandlerKeyEnum.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
