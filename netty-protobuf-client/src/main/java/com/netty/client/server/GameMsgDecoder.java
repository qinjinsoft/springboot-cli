package com.netty.client.server;

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
        ctx.fireChannelRead(frame);
//        GeneratedMessageV3 cmd = null;
//        cmd = MessageBuf.msg_req.parseFrom(msgBody);
//        switch (msgCode) {
//            case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
//                cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
//                break;
//
//            case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
//                cmd = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(msgBody);
//                break;
//
//            case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
//                cmd = GameMsgProtocol.UserMoveToCmd.parseFrom(msgBody);
//                break;
//        }

//        if (null != cmd) {
//            ctx.fireChannelRead(cmd);
//        }
    }
}
