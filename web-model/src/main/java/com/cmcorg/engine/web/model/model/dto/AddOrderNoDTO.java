package com.cmcorg.engine.web.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddOrderNoDTO extends NotEmptyIdSet {

    @NotNull
    @Schema(description = "统一加减的数值")
    private Integer number;

}
