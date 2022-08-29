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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        MessageBuf.msg_req msg_req = MessageBuf.msg_req.newBuilder().setCmd(1000).build();
        log.info("回复服务端消息：{}",msg_req);
        ctx.writeAndFlush(msg_req);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到服务端消息, msgClazz = " + msg.getClass().getName() + ", msg = " + msg);
        log.info("收到服务端消息：{}",msg);
    }
}
