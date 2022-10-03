package com.cmcorg.engine.web.email.exception;

import com.cmcorg.engine.web.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {
    EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER(300011, "操作失败：邮箱不存在，请重新输入"), //
    ;

    private int code;
    private String msg;
}
