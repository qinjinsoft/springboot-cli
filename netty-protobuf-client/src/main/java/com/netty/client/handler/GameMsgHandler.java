package com.netty.client.handler;

import com.netty.client.protobuf.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义的消息处理器
 */
@Slf4j
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 客户端信道数组, 一定要使用 static, 否则无法实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        _channelGroup.add(ctx.channel());
        MessageBuf.msg_req.Builder builder = MessageBuf.msg_req.newBuilder().setCmd(2000);
        log.info("回复客户端消息：{}",builder);
        ctx.writeAndFlush(builder.build());
        _channelGroup.writeAndFlush(builder.build());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        _channelGroup.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息, msgClazz = " + msg.getClass().getName() + ", msg = " + msg);
        log.info("收到客户端消息：{}",msg);
    }
}
