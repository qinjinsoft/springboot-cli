package com.netty.client.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

/**
 * TODO
 *
 * @author qinjin
 * @date 2022/8/2 17:24
 */
@Data
public class NetMessage {
    public int messageId;


    public String email;
    public String password;

    public String nickName;

    public String toJson() {
        return JSON.toJSONString(this, SerializerFeature.IgnoreErrorGetter);
    }
}
