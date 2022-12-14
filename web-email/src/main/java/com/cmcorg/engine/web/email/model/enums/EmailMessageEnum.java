package com.cmcorg.engine.web.email.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "邮件消息枚举类")
public enum EmailMessageEnum {

    SIGN_UP("邮箱注册", "尊敬的用户您好，您本次注册的验证码是（10分钟内有效）：{}"), //
    UPDATE_PASSWORD("修改密码", "尊敬的用户您好，您正在进行修改密码操作，您本次修改密码的验证码是（10分钟内有效）：{}"), //
    UPDATE_EMAIL("修改邮箱", "尊敬的用户您好，您正在进行修改邮箱操作，您本次修改邮箱的验证码是（10分钟内有效）：{}"), //
    SIGN_DELETE("账号注销", "尊敬的用户您好，您账号注销的验证码是（10分钟内有效）：{}"), //
    FORGOT_PASSWORD("忘记密码", "尊敬的用户您好，您正在进行忘记密码操作，您本次忘记密码的验证码是（10分钟内有效）：{}"), //
    BIND_EMAIL("绑定邮箱", "尊敬的用户您好，您正在进行绑定邮箱操作，您本次绑定邮箱的验证码是（10分钟内有效）：{}"), //

    ;

    private String subject; // 主题
    private String contentTemp; // 消息模板

}
