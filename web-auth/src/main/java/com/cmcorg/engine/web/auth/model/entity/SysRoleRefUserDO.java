package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_role_ref_user")
@Data
@Schema(description = "角色，用户关联表")
public class SysRoleRefUserDO {

    @TableId
    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "用户id")
    private Long userId;

}
