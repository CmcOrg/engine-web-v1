package com.cmcorg.engine.web.auth.exception;

import cn.hutool.core.map.MapUtil;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 参数校验异常-1
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResultVO<?> handlerValidException(MethodArgumentNotValidException e) {

        // 返回详细的参数校验错误信息
        Map<String, String> map = MapUtil.newHashMap(e.getBindingResult().getFieldErrors().size());
        BindingResult bindingResult = e.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        try {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR, map); // 这里肯定会抛出 BaseException异常
        } catch (BaseException baseException) {
            return getBaseExceptionApiResult(baseException);
        }

        return null; // 这里不会执行，只是为了通过语法检查
    }

    /**
     * 参数校验异常-2
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ApiResultVO<?> handlerIllegalArgumentException(IllegalArgumentException e) {

        try {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR, e.getMessage()); // 这里肯定会抛出 BaseException异常
        } catch (BaseException baseException) {
            return getBaseExceptionApiResult(baseException);
        }

        return null; // 这里不会执行，只是为了通过语法检查
    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(value = BaseException.class)
    public ApiResultVO<?> handlerBaseException(BaseException e) {

        e.printStackTrace();

        return getBaseExceptionApiResult(e);
    }

    /**
     * 缺省异常处理，直接提示系统异常
     */
    @ExceptionHandler(value = Throwable.class)
    public ApiResultVO<?> handlerThrowable(Throwable e) {

        e.printStackTrace();

        try {
            ApiResultVO.sysError(); // 这里肯定会抛出 BaseException异常
        } catch (BaseException baseException) {
            return getBaseExceptionApiResult(baseException);
        }

        return null; // 这里不会执行，只是为了通过语法检查
    }

    private ApiResultVO<?> getBaseExceptionApiResult(BaseException e) {
        return e.getApiResultVO();
    }

}
