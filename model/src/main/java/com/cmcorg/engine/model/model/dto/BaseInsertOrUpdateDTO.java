package com.cmcorg.engine.model.model.dto;

import com.cmcorg.engine.model.model.annotation.RequestField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseInsertOrUpdateDTO {

    @RequestField(formIgnoreFlag = true)
    @Min(1)
    @Schema(description = "主键id")
    private Long id;

}
