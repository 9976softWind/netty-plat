package com.hf.netty.part4;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author tdw
 * @date 2025.5.21
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    // 初始化：向管道中加入处理器
    protected void initChannel(SocketChannel ch) throws Exception {
        //得到管道
        ChannelPipeline pipeline = ch.pipeline();
        System.out.println("客户端连接：" + pipeline.channel().remoteAddress());
        // 向管道内加入处理器
        pipeline.addLast("MyHttpServerCode",new HttpServerCodec()) // HttpServerCodec是netty提供的一个基于http协议的编解码器
                .addLast("MyTestHttpServerHandler",new TestHttpServerHandler()); //添加一个自定义的handler

        System.out.println("ok~~~~~");
    }
}
