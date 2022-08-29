package com.netty.client.server;

import com.netty.client.config.ClientProperties;
import com.netty.client.handler.GameMsgHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;

/**
 * 启动 Broker
 *
 * @author qiding
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TcpClient implements ITcpClient {

    private final ClientProperties clientProperties;

    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    private SocketChannel socketChannel;

    private Bootstrap bootstrap;
    /**
     * 记录当前连接的服务器ip（用于重连）
     */
    public static String connectedIp;
    /**
     * 记录当前连接的服务器端口（用于重连）
     */
    public static Integer connectedPort;

    @Override
    public void start() throws Exception {
        log.info("初始化 MQTT Client ...");
        this.tcpClient();
    }

    @Override
    public void reconnect() throws Exception {
        if (socketChannel != null && socketChannel.isActive()) {
            socketChannel.close();
            this.connect(connectedIp, connectedPort);
        }

    }

    public void disconnect() {
        if (socketChannel != null && socketChannel.isActive()) {
            socketChannel.close();
        }
    }

    /**
     * mqttBroker初始化
     */
    private void tcpClient() {
        try {
            bootstrap = new Bootstrap()
                    .remoteAddress("127.0.0.1", clientProperties.getClientPort())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpServerCodec(), // Http 服务器编解码器
                                    new HttpObjectAggregator(65535), // 内容长度限制
                                    new GameMsgDecoder(), // 自定义的消息解码器
                                    new GameMsgEncoder(), // 自定义的消息编码器
                                    new GameMsgHandler() // 自定义的消息处理器
                            );
                        }
                    })
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true);
            bootstrap.group(WORKER_GROUP);
            this.connect(clientProperties.getServerIp(), clientProperties.getServerPort());
        } catch (Exception e) {
            e.printStackTrace();
            WORKER_GROUP.shutdownGracefully();
        }
    }

    /**
     * 连接服务器
     */
    public void connect(String ip, Integer port) throws InterruptedException {
        this.disconnect();
        connectedIp = ip;
        connectedPort = port;
        ChannelFuture future = bootstrap.connect(connectedIp, connectedPort).sync();
        if (future.isSuccess()) {
            socketChannel = (SocketChannel) future.channel();
            log.info("connect server success");
        }
    }


    /**
     * 销毁
     */
    @PreDestroy
    @Override
    public void destroy() {
        WORKER_GROUP.shutdownGracefully();
        socketChannel.closeFuture();
    }

    /**
     * 获取频道
     */
    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }
}
