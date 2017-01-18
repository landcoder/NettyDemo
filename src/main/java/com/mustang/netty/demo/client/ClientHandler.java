package com.mustang.netty.demo.client;

import com.mustang.netty.common.CustomHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端处理器
 * Created by Mustang on 17/1/16.
 */
public class ClientHandler extends CustomHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Client client;
    public ClientHandler(Client client) {
        super("client");
        this.client = client;
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        byteBuf.skipBytes(5);
        byteBuf.readBytes(data);
        String content = new String(data);
        logger.info("{} get msg: {}", name, content);
    }

    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        sendPingMsg(ctx); //客户端捕获all 发送ping给服务器
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        client.doConnect(); //客户端退出重连
    }
}
