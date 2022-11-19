package com.cmcorg.engine.web.wx.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import com.cmcorg.engine.web.wx.model.vo.WxAccessTokenVO;
import com.cmcorg.engine.web.wx.model.vo.WxBaseVO;
import com.cmcorg.engine.web.wx.model.vo.WxGetPhoneByCodeVO;
import com.cmcorg.engine.web.wx.properties.WxProperties;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WxUtil {

    private static RedissonClient redissonClient;
    private static WxProperties wxProperties;

    public WxUtil(RedissonClient redissonClient, WxProperties wxProperties) {
        WxUtil.redissonClient = redissonClient;
        WxUtil.wxProperties = wxProperties;
    }

    /**
     * code换取用户手机号，每个code只能使用一次，code的有效期为5min
     */
    public static String getPhoneByCode(String code) {

        String accessToken = getAccessToken();

        JSONObject formJson = JSONUtil.createObj().set("code", code);

        String postStr = HttpUtil
            .post("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken,
                formJson.toString());

        WxGetPhoneByCodeVO wxGetPhoneByCodeVO = JSONUtil.toBean(postStr, WxGetPhoneByCodeVO.class);

        checkWxVO(wxGetPhoneByCodeVO, "用户手机号"); // 检查：微信回调 vo对象

        return wxGetPhoneByCodeVO.getPhone_info().getPhoneNumber();

    }

    /**
     * 获取：微信小程序全局唯一后台接口调用凭据
     */
    private static String getAccessToken() {

        RBucket<String> bucket = redissonClient.getBucket(RedisKeyEnum.WX_ACCESS_TOKEN_CACHE.name());

        String accessToken = bucket.get();

        if (StrUtil.isBlank(accessToken)) {

            String jsonStr = HttpUtil.get(
                "https://api.weixin.qq.com/cgi-bin/token?appid=" + wxProperties.getAppId() + "&secret=" + wxProperties
                    .getSecret() + "&grant_type=client_credential");

            WxAccessTokenVO wxAccessTokenVO = JSONUtil.toBean(jsonStr, WxAccessTokenVO.class);

            checkWxVO(wxAccessTokenVO, "accessToken"); // 检查：微信回调 vo对象

            // 存入 redis中
            bucket.set(wxAccessTokenVO.getAccess_token(), wxAccessTokenVO.getExpires_in(), TimeUnit.SECONDS);

            accessToken = wxAccessTokenVO.getAccess_token();

        }

        return accessToken;

    }

    /**
     * 检查：微信回调 vo对象
     */
    private static void checkWxVO(WxBaseVO wxBaseVO, String msg) {

        if (wxBaseVO.getErrcode() != null && wxBaseVO.getErrcode() != 0) {

            throw new RuntimeException(
                "微信：获取【" + msg + "】失败，errcode：【" + wxBaseVO.getErrcode() + "】，errmsg：【" + wxBaseVO.getErrmsg() + "】");

        }

    }

}
