package com.mustang.netty.demo.server;

import com.mustang.netty.common.CustomHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端处理器
 * Created by Mustang on 17/1/16.
 */
public class ServerHandler extends CustomHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ServerHandler() {
        super("server");
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes() - 5];
        ByteBuf responseBuf = Unpooled.copiedBuffer(buf);
        buf.skipBytes(5);
        buf.readBytes(data);
        String content = new String(data);
        logger.info("{} get msg: {}" , name, content);
        channelHandlerContext.write(responseBuf);
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        logger.error("client {} time out, close it!", ctx.channel().remoteAddress().toString());
        ctx.close();
    }
}
