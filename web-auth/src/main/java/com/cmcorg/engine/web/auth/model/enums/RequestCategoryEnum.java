package com.cmcorg.engine.web.auth.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@Schema(description = "请求类别")
public enum RequestCategoryEnum {

    H5((byte)1, "H5（网页端）"), //
    APP((byte)2, "APP（移动端）"), //
    PC((byte)3, "PC（桌面程序）"), //
    WX_APP((byte)4, "微信小程序"), //

    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    @NotNull
    public static RequestCategoryEnum getByCode(Byte code) {
        if (code == null) {
            return H5;
        }
        for (RequestCategoryEnum item : RequestCategoryEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return H5;
    }

}
