package com.cmcorg.engine.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_role_ref_menu")
@Data
@Schema(description = "角色，菜单关联表")
public class SysRoleRefMenuDO {

    @TableId
    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "菜单id")
    private Long menuId;

}
