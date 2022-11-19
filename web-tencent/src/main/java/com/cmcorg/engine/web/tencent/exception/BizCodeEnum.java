package com.cmcorg.engine.web.tencent.exception;

import com.cmcorg.engine.web.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    PHONE_DOES_NOT_EXIST_PLEASE_RE_ENTER(300011, "操作失败：手机号码不存在，请重新输入"), //
    PHONE_NOT_REGISTERED(300021, "操作失败：手机号码未注册，请重新输入"), //

    ;

    private int code;
    private String msg;
}
