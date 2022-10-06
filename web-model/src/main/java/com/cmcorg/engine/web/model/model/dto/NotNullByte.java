package com.cmcorg.engine.web.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class NotNullByte {

    @Min(1)
    @NotNull
    @Schema(description = "值")
    private Byte value;

}
