package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.web.auth.model.constant.WebModelConstant;
import com.cmcorg.engine.web.model.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.model.annotation.RequestField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_TWO)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_role")
@Data
@Schema(description = "角色主表")
public class SysRoleDO extends BaseEntity {

    @RequestField(tableTitle = "角色名")
    @Schema(description = "角色名（不能重复）")
    private String name;

    @RequestField(tableTitle = "默认角色")
    @Schema(description = "是否是默认角色，备注：只会有一个默认角色")
    private Boolean defaultFlag;

}
