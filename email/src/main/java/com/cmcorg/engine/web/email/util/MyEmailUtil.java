package com.cmcorg.engine.web.email.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.properties.CommonProperties;
import com.cmcorg.engine.web.email.exception.BizCodeEnum;
import com.cmcorg.engine.web.email.model.enums.EmailMessageEnum;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * 邮箱工具类
 */
@Component
public class MyEmailUtil {

    private static String platformName;

    public MyEmailUtil(CommonProperties commonProperties) {
        MyEmailUtil.platformName = "【" + commonProperties.getPlatformName() + "】";
    }

    /**
     * 发送邮件
     */
    @Nullable
    public static String send(String to, EmailMessageEnum emailMessageEnum, String content, boolean isHtml) {

        try {

            // 消息内容，加上统一的前缀
            content = platformName + StrUtil.format(emailMessageEnum.getContentTemp(), content);

            return MailUtil.send(to, emailMessageEnum.getSubject(), content, isHtml);

        } catch (MailException e) {

            if (e.getMessage() != null && e.getMessage().contains("Invalid Addresses")) {
                ApiResultVO.error(BizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER);
            } else {
                e.printStackTrace();
            }
            return null;
        }

    }

}
