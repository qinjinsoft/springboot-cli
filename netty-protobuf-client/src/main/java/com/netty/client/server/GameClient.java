package com.netty.client.server;

import com.netty.client.handler.GameMsgHandler;
import com.netty.client.protobuf.MessageBuf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 启动 Broker
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GameClient {

    public void start() throws URISyntaxException, InterruptedException {
        log.info("初始化 MQTT Client ...");
        this.gameClient();
    }

    public Channel channel = null;
    /**
     * mqttBroker初始化
     */
    private void gameClient() throws URISyntaxException, InterruptedException {
        URI uri = new URI("ws://127.0.0.1:8013/websocket");


        //netty基本操作，线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //netty基本操作，启动类
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler((new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch){
                        ch.pipeline().addLast(
                                new HttpClientCodec(), // Http 服务器编解码器
                                new HttpObjectAggregator(65535), // 内容长度限制
//                                new GameMsgDecoder(), // 自定义的消息解码器
                                new GameMsgEncoder(), // 自定义的消息编码器
                                new GameMsgHandler() // 自定义的消息处理器
                        );
                    }
                }));
        //websocke连接的地址，/hello是因为在服务端的websockethandler设置的
        URI websocketURI = new URI("ws://127.0.0.1:8013/websocket");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        //客户端与服务端连接的通道，final修饰表示只会有一个
        channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
        GameMsgHandler handler = channel.pipeline().get(GameMsgHandler.class);
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
//        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();

    }
    /**
     * 获取频道
     */
    public Channel getSocketChannel() {
        return this.channel;
    }
}
