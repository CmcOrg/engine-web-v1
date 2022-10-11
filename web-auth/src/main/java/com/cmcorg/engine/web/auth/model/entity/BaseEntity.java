package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "实体类基类")
public class BaseEntity extends BaseEntityNoId {

    /**
     * 这里是自定义的主键 id
     */
    @RequestField(tableIgnoreFlag = true)
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private Long id;

}
