package com.cmcorg.engine.web.netty.boot.handler;

import cn.hutool.core.map.MapUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = LogTopicConstant.NETTY)
public abstract class AbstractNettyServerHandler extends ChannelInboundHandlerAdapter {

    // 没有进行身份认证的通道，一般这种通道，业务可以忽略，备注：一定时间内，会关闭此类型通道
    private static final Map<String, Channel> NOT_SECURITY_CHANNEL_MAP = MapUtil.newConcurrentHashMap();
    // 通道，连接时间，时间戳
    private static final AttributeKey<Long> CREATE_TIME = AttributeKey.valueOf("createTime");
    // 移除规定时间内，没有身份认证成功的通道，中的【规定时间】
    public static final long SECURITY_EXPIRE_TIME = BaseConstant.SECOND_20_EXPIRE_TIME;

    // 进行了身份认证的通道
    private static final Map<Long, Channel> USER_ID_CHANNEL_MAP = MapUtil.newConcurrentHashMap();
    // userId key
    private static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf("userId");

    /**
     * 获取：进行了身份认证的通道
     */
    protected static Map<Long, Channel> getUserIdChannelMap() {
        return USER_ID_CHANNEL_MAP;
    }

    /**
     * 定时：移除规定时间内，没有身份认证成功的通道
     * 备注：就算子类加了 @Component注解，本方法，规定时间内也只会被执行一次
     */
    @Scheduled(fixedRate = 10 * 1000)
    private void removeNotSecurityChannelMap() {
        for (Map.Entry<String, Channel> item : NOT_SECURITY_CHANNEL_MAP.entrySet()) {
            if ((System.currentTimeMillis() - item.getValue().attr(CREATE_TIME).get()) > SECURITY_EXPIRE_TIME) {
                item.getValue().close(); // 关闭通道
            }
        }
    }

    /**
     * 连接成功时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().attr(CREATE_TIME).set(System.currentTimeMillis());
        NOT_SECURITY_CHANNEL_MAP.put(ctx.channel().id().asLongText(), ctx.channel());
        log.info("通道连接成功，通道 id：{}，当前没有进行身份认证的通道总数：{}", ctx.channel().id().asLongText(),
            NOT_SECURITY_CHANNEL_MAP.size());
        super.channelActive(ctx);
    }

    /**
     * 调用 close等操作，连接断开时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ctx.channel().attr(USER_ID_KEY).get();
        if (userId != null) {
            USER_ID_CHANNEL_MAP.remove(userId);
        } else {
            NOT_SECURITY_CHANNEL_MAP.remove(ctx.channel().id().asLongText());
        }
        log.info("通道断开连接，用户 id：{}，通道 id：{}，当前没有进行身份认证的通道总数：{}，当前进行了身份认证的通道总数：{}", ctx.channel().attr(USER_ID_KEY).get(),
            ctx.channel().id().asLongText(), NOT_SECURITY_CHANNEL_MAP.size(), USER_ID_CHANNEL_MAP.size());
        super.channelInactive(ctx);
    }

    /**
     * 发生异常时，比如：远程主机强迫关闭了一个现有的连接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        super.exceptionCaught(ctx, e);
        ctx.close(); // 会执行：channelInactive 方法
    }

    /**
     * 收到消息时
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        try {

            Long userId = ctx.channel().attr(USER_ID_KEY).get();
            if (userId == null) {
                log.info("处理身份认证的消息，通道 id：{}", ctx.channel().id().asLongText());
                // 处理：身份认证的消息，成功之后调用：consumer 即可
                handlerSecurityMessage(msg, ctx.channel(), aLong -> {
                    // 身份认证成功，之后的处理
                    ctx.channel().attr(USER_ID_KEY).set(aLong);
                    NOT_SECURITY_CHANNEL_MAP.remove(ctx.channel().id().asLongText());
                    USER_ID_CHANNEL_MAP.put(aLong, ctx.channel());
                    log.info("处理身份认证的消息成功，用户 id：{}，通道 id：{}，当前没有进行身份认证的通道总数：{}，当前进行了身份认证的通道总数：{}",
                        ctx.channel().attr(USER_ID_KEY).get(), ctx.channel().id().asLongText(),
                        NOT_SECURITY_CHANNEL_MAP.size(), USER_ID_CHANNEL_MAP.size());
                });
                return;
            }

            // 把 userId设置到：security的上下文里面
            SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userId, null, null));

            handlerMessage(msg);// 处理：进行了身份认证的通道的消息

        } catch (Throwable e) {
            exceptionAdvice(e); // 处理业务异常
        } finally {
            ReferenceCountUtil.release(msg); // 释放资源
        }
    }

    /**
     * 处理：身份认证的消息
     */
    public abstract void handlerSecurityMessage(Object msg, Channel channel, Consumer<Long> consumer);

    /**
     * 处理：进行了身份认证的通道的消息
     */
    public abstract void handlerMessage(Object msg);

    /**
     * 处理业务异常
     */
    public abstract void exceptionAdvice(Throwable e);

}
