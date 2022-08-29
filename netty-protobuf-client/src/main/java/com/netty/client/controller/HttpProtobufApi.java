package com.netty.client.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.netty.client.protobuf.MessageBuf;
import com.netty.client.server.GameClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟发送api
 *
 * @author qiding
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class HttpProtobufApi {

    private final GameClient gameClient;
    /**
     * 消息发布
     */
    @GetMapping("/send")
    public String send() {
        NetMessage request = new NetMessage();
        request.setType("9");
        request.setName("test");
        MessageBuf.msg_req msg_req = MessageBuf.msg_req.newBuilder().setCmd(2001).setData(ByteString.copyFromUtf8(JSONObject.toJSONString(request))).build();
        gameClient.getSocketChannel().writeAndFlush(msg_req);
        return "发送成功";
    }

}
