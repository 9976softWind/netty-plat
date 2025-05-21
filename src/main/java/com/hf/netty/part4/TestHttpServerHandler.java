package com.hf.netty.part4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @author tdw
 * @date 2025.5.21
 * SimpleChannelInboundHandler继承ChannelInboundHandlerAdapter
 * HttpObject：客户端和服务器端相互通讯的数据被封装成HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    // 读取客户端数据
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        /**
         * Net Assistant调试
         * 47 45 54 20 2F 20 48 54 54 50 2F 31 2E 31 0D 0A
         * 48 6F 73 74 3A 20 65 78 61 6D 70 6C 65 2E 63 6F 6D 0D 0A
         * 0D 0A
         *
         */
        System.out.println("对应的channel=" + ctx.channel() + " pipeline=" + ctx
                .pipeline() + " 通过pipeline获取channel" + ctx.pipeline().channel());

        System.out.println("当前ctx的handler=" + ctx.handler());

        if(msg instanceof HttpRequest){
            System.out.println("ctx 类型=" + ctx.getClass());
            System.out.println("pipeline hashcode" + ctx.pipeline().hashCode() + " TestHttpServerHandler hash=" + this.hashCode());
            System.out.println("msg 类型=" + msg.getClass());
            System.out.println("客户端地址" + ctx.channel().remoteAddress());
            HttpRequest httpRequest = (HttpRequest) msg;
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求了 favicon.ico, 不做响应");
                return;
            }
            //回复信息给浏览器 [http协议]
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello,i'm server", CharsetUtil.UTF_8);
            // 构造http响应
            FullHttpResponse  response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            //将构建好 response返回
            ctx.writeAndFlush(response);
        }
    }

}
