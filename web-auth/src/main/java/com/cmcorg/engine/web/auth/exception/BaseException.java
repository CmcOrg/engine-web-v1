package com.cmcorg.engine.web.auth.exception;

import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {

    private ApiResultVO<?> apiResultVO;

    public BaseException(ApiResultVO<?> apiResult) {
        super(JSONUtil.toJsonStr(apiResult)); // 把信息封装成json格式
        setApiResultVO(apiResult);
    }

}
