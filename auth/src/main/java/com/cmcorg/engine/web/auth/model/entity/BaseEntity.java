package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "实体类基类")
public class BaseEntity {

    /**
     * IdType.ASSIGN_ID，就是：{@link MybatisPlusConfiguration#idGenerator()}
     */
    @RequestField(tableIgnoreFlag = true)
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人id")
    private Long createId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改人id")
    private Long updateId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @RequestField(tableIgnoreFlag = true)
    @Version
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "乐观锁")
    private Integer version;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @RequestField(tableIgnoreFlag = true)
    // 如果要加 @TableLogic，建议继承本类，再给子类的字段上面加这个注解，因为不是每个业务都需要逻辑删除，不建议使用逻辑删除，而是采用直接删除，删除的数据，可以存到其他地方，不然数据量会越来越大
    @Schema(description = "是否逻辑删除")
    private Boolean delFlag;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "备注")
    private String remark;

}
