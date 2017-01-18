package com.mustang.netty.demo.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Desc
 * Created by Mustang on 17/1/16.
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private Client client;

    public ClientInitializer(Client client) {
        this.client = client;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new IdleStateHandler(0, 0, 5));
        p.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, -4, 0));
//        p.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        p.addLast(new ClientHandler(client));
    }
}
