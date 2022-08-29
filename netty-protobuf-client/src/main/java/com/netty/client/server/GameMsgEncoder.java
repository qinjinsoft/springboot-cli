package com.netty.client.server;

import com.google.protobuf.GeneratedMessageV3;
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
        if (null == msg ||
                !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }
        log.info("GameMsgEncoder--->符合条件write:{}",msg);
        int msgCode = -1;

//        if (msg instanceof GameMsgProtocol.UserEntryResult) {
//            msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
//        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
//            msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
//        } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
//            msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
//        } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
//            msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
//        } else {
//            LOGGER.error("无法识别的消息类型, msgClazz = " + msg.getClass().getName());
//            return;
//        }

        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeShort((short)0); // 写出消息长度, 目前写出 0 只是为了占位
        byteBuf.writeShort((short)msgCode); // 写出消息编号
        byteBuf.writeBytes(msgBody); // 写出消息体

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx, frame, promise);
    }
}
