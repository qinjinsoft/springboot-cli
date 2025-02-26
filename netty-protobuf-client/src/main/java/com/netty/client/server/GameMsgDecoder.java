package com.netty.client.server;

import com.google.protobuf.GeneratedMessageV3;
import com.netty.client.protobuf.MessageBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息解码器
 */
@Slf4j
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        log.info("GameMsgDecoder--->channelActive");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        log.info("GameMsgDecoder--->channelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead--->{}",msg);
        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        // WebSocket 二进制消息会通过 HttpServerCodec 解码成 BinaryWebSocketFrame 类对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = frame.content();

        byteBuf.readShort(); // 读取消息的长度
        int msgCode = byteBuf.readShort(); // 读取消息的编号

        // 拿到消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);
        log.info("channelRead---msgBody>{}",msgBody);
        log.info("channelRead---frame>{}",frame);
        GeneratedMessageV3 cmd = null;

        switch (msgCode) {
            case MessageBuf.GAME.G_CMCheckVersion_VALUE:
                cmd = MessageBuf.cm_check_version.parseFrom(msgBody);
                break;
            default:
                cmd = MessageBuf.msg_req.parseFrom(msgBody);
                break;
        }

        if (null != cmd) {
            ctx.fireChannelRead(cmd);
        }
    }
}
