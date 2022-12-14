package com.cmcorg.engine.web.auth.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.model.model.constant.ParamConstant;
import org.springframework.stereotype.Component;

@Component
public class MyRsaUtil {

    /**
     * 非对称：解密
     */
    public static String rsaDecrypt(String str) {

        String paramValue = SysParamUtil.getValueById(ParamConstant.RSA_PRIVATE_KEY_ID); // 获取非对称加密，私钥

        return rsaDecrypt(str, paramValue); // 返回解密之后的 字符串
    }

    /**
     * 非对称：解密
     */
    public static String rsaDecrypt(String str, String privateKey) {

        if (StrUtil.isBlank(str)) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        if (StrUtil.isBlank(privateKey)) {
            ApiResultVO.sysError();
        }

        RSA rsa = new RSA(privateKey, null);

        String decryptStr = null;
        try {
            decryptStr = rsa.decryptStr(str, KeyType.PrivateKey);
        } catch (CryptoException e) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        return decryptStr; // 返回解密之后的 字符串
    }

}
