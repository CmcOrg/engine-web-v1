package com.cmcorg.engine.web.auth.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.cmcorg.engine.web.auth.properties.AuthProperties;
import com.cmcorg.engine.web.auth.util.ResponseUtil;
import com.cmcorg.engine.web.auth.util.SysParamUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import com.cmcorg.engine.web.model.model.constant.ParamConstant;
import com.cmcorg.engine.web.redisson.model.enums.RedisKeyEnum;
import com.cmcorg.engine.web.util.util.SeparatorUtil;
import lombok.SneakyThrows;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ip 拦截器
 */
@Order(value = Integer.MIN_VALUE)
@Component
@WebFilter(urlPatterns = "/*")
public class IpFilter implements Filter {

    @Resource
    AuthProperties authProperties;
    @Resource
    RedissonClient redissonClient;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        if (BooleanUtil.isFalse(authProperties.getIpFilterEnable())) {
            return;
        }

        String ip = ServletUtil.getClientIP((HttpServletRequest)request);

        // ip 请求速率处理
        String timeStr = ipCheckHandler(ip);

        if (timeStr == null) {
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out((HttpServletResponse)response, "操作次数过多，请在 " + timeStr + "后，再进行操作");
        }

    }

    /**
     * ip 请求速率处理
     * 返回 null，则表示不在黑名单，不为 null，则会返回剩余移除黑名单的倒计时时间（字符串）
     */
    private String ipCheckHandler(String ip) {

        RBucket<String> blackIpRedisBucket = redissonClient.getBucket(RedisKeyEnum.PRE_IP_BLACK + ip);

        // 判断是否在 黑名单里
        long remainTimeToLive = blackIpRedisBucket.remainTimeToLive();

        if (remainTimeToLive > -1) {
            // 如果在 黑名单里，则返回剩余时间
            return DateUtil.formatBetween(remainTimeToLive, BetweenFormatter.Level.SECOND); // 剩余时间（字符串）
        }

        return setRedisTotal(ip, blackIpRedisBucket);

    }

    /**
     * 给 redis中 ip设置 请求次数
     */
    private String setRedisTotal(String ip, RBucket<String> blackIpRedisBucket) {

        // ip 请求速率：多少秒钟，一个 ip可以请求多少次，用冒号隔开的
        String ipTotalCheckValue = SysParamUtil.getValueById(ParamConstant.IP_REQUESTS_PER_SECOND_ID);

        if (ipTotalCheckValue == null) {
            return null;
        }

        List<String> splitTrimList = StrUtil.splitTrim(ipTotalCheckValue, SeparatorUtil.COLON_SEPARATOR);
        if (splitTrimList.size() != 2) {
            return null;
        }

        Integer timeInt = Convert.toInt(splitTrimList.get(0)); // 多少秒钟
        if (timeInt == null || timeInt <= 0) {
            return null;
        }
        Integer total = Convert.toInt(splitTrimList.get(1)); // 可以请求多少次
        if (total == null || total <= 0) {
            return null;
        }

        RAtomicLong atomicLong = redissonClient.getAtomicLong(RedisKeyEnum.PRE_IP_TOTAL_CHECK + ip);

        long incrementAndGet = atomicLong.incrementAndGet(); // 次数 + 1

        if (incrementAndGet == 1) {
            atomicLong.expire(Duration.ofSeconds(timeInt)); // 等于 1表示，是第一次访问，则设置过期时间
            return null;
        }

        if (incrementAndGet > total) {
            atomicLong.delete(); // 移除：ip的计数
            blackIpRedisBucket.set("黑名单 ip", BaseConstant.DAY_1_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            return "1天";
        }

        return null;
    }
}
