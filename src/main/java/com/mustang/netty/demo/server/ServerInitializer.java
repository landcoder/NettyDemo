package com.mustang.netty.demo.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * Desc
 * Created by Mustang on 17/1/16.
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new IdleStateHandler(10, 0, 0));
        p.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, -4, 0));
//        p.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        p.addLast(new ServerHandler());
    }
}

