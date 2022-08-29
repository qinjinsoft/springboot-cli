package com.netty.client.server;

import com.google.protobuf.GeneratedMessageV3;
import com.netty.client.protobuf.MessageBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息编码器
 */
@Slf4j
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("GameMsgEncoder--->write:{}",msg);
        if (null == ctx ||
                null == msg) {
            return;
        }

        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            // 消息编码
            int msgCode = -1;

            if (msg instanceof MessageBuf.msg_req) {
                msgCode = MessageBuf.GAME.G_CMCheckVersion_VALUE;
            } else if (msg instanceof MessageBuf.cm_check_version) {
                msgCode = MessageBuf.GAME.G_CMCheckVersion_VALUE;
            } else {
                log.error("无法识别的消息类型, msgClazz = {}",msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }
            log.info("GameMsgEncoder--->msgCode:{}",msgCode);
            // 消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeShort((short) msgBody.length); // 消息的长度
            byteBuf.writeShort((short) msgCode); // 消息编号
            byteBuf.writeBytes(msgBody); // 消息体

            // 写出 ByteBuf
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            super.write(ctx, outputFrame, promise);
        } catch (Exception ex) {
            // 记录错误日志
            log.error(ex.getMessage(), ex);
        }
    }
}
