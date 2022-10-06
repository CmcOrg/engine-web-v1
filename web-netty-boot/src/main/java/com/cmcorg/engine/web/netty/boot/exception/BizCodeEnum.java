package com.cmcorg.engine.web.netty.boot.exception;

import com.cmcorg.engine.web.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    PATH_NOT_FOUND(300011, "路径未找到：{}"), //

    ;

    private int code;
    private String msg;

}
