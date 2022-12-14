package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import com.cmcorg.engine.web.model.generate.model.constant.WebModelConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_TWO)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_param")
@Data
@Schema(description = "系统参数主表")
public class SysParamDO extends BaseEntity {

    @RequestField(formTitle = "配置名")
    @Schema(description = "配置名，以 id为不变值进行使用，不要用此属性")
    private String name;

    @RequestField(hideInSearchFlag = true)
    @Schema(description = "值")
    private String value;

}
