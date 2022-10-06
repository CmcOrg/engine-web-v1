package com.cmcorg.engine.web.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotNullByteAndId extends NotNullId {

    @Min(1)
    @NotNull
    @Schema(description = "值")
    private Byte value;

}
