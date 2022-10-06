package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "树形结构实体类基类")
public class BaseEntityTree<T> extends BaseEntity {

    @RequestField(formTitle = "排序号", hideInSearchFlag = true)
    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @RequestField(tableIgnoreFlag = true)
    @Schema(description = "父节点id（顶级则为0）")
    private Long parentId;

    @RequestField(tableIgnoreFlag = true)
    @TableField(exist = false)
    @Schema(description = "子节点")
    private List<T> children;

}
