package com.hf.netty.part2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author tdw
 * @date 2025.5.21
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //读取数据实际(这里我们可以读取客户端发送的消息)
    /**
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道pipeline , 通道channel, 地址
     * 2. Object msg: 就是客户端发送的数据 默认Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //将 msg 转成一个 ByteBuf
//        ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer.
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送消息：" + buf.toString(CharsetUtil.UTF_8));



        // 比如这里我们有一个非常耗时长的业务-> 异步执行 -> 提交该channel 对应的
        // NIOEventLoop 的 taskQueue中,

        //方案1：用户程序自定义的普通任务
        ctx.channel().eventLoop().execute(()->{
            try {
                Thread.sleep(5*1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello1,client", CharsetUtil.UTF_8));
                System.out.println("channel code = " + ctx.channel().hashCode());
            } catch (Exception ex) {
                System.out.println("发生异常" + ex.getMessage());
            }
        });
        ctx.channel().eventLoop().execute(()->{
            try {
                Thread.sleep(5*1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello2,client", CharsetUtil.UTF_8));
                System.out.println("channel code = " + ctx.channel().hashCode());
            } catch (Exception ex) {
                System.out.println("发生异常" + ex.getMessage());
            }
        });

        // 方案2，用户自定义定时任务
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello3,client",CharsetUtil.UTF_8));
                    System.out.println("channel code = " + ctx.channel().hashCode());
                }catch (Exception ex){
                    System.out.println("发生异常" + ex.getMessage());
                }
            }
        }, 5, TimeUnit.SECONDS);
        System.out.println("go on ...");
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush 是 write + flush
        //将数据写入到缓存，并刷新
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,client",CharsetUtil.UTF_8) );
    }

    // 客户端断连
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("客户端断开连接：" + channel.remoteAddress() + "_" +channel.hashCode());
    }

    //处理异常, 一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
