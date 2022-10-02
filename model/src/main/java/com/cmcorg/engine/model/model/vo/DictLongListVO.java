package com.cmcorg.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictLongListVO {

    @Schema(description = "显示用")
    private String label;

    @Schema(description = "传值用")
    private Long value;

}
