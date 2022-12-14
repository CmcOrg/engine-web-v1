package com.cmcorg.engine.web.auth.util;

import cn.hutool.core.convert.Convert;
import com.cmcorg.engine.web.auth.model.constant.BaseConfigurationConstant;
import com.cmcorg.engine.web.auth.model.enums.RequestCategoryEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    /**
     * 获取当前上下文的 request对象
     */
    @Nullable
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return requestAttributes.getRequest();
    }

    /**
     * 获取请求类别
     */
    @NotNull
    public static RequestCategoryEnum getRequestCategoryEnum() {
        return getRequestCategoryEnum(getRequest());
    }

    /**
     * 获取请求类别
     */
    @NotNull
    public static RequestCategoryEnum getRequestCategoryEnum(HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) {
            return RequestCategoryEnum.H5;
        }
        return RequestCategoryEnum
            .getByCode(Convert.toByte(httpServletRequest.getHeader(BaseConfigurationConstant.REQUEST_HEADER_CATEGORY)));
    }

}
