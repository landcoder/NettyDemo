package com.mustang.netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公用处理器
 * Created by Mustang on 17/1/16.
 */
public abstract class CustomHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected String name;

    private int heartbeatCount = 0; //心跳统计

    public CustomHandler(String name) {
        this.name = name;
    }

    /**
     * 读取信息
     * @param context
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext context, ByteBuf byteBuf) throws Exception {
        if (byteBuf.getByte(4) == Constants.PING_MSG) {
            sendPongMsg(context);
        } else if (byteBuf.getByte(4) == Constants.PONG_MSG){
            logger.info("client{} get pong msg from server:{}", name, context.channel().remoteAddress());
        } else {
            handleData(context, byteBuf);
        }
    }

    /**
     * 发送ping消息
     * @param context
     */
    protected void sendPingMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(Constants.PING_MSG);
//        buf.writeBytes("\n".getBytes());
        buf.retain();
        context.writeAndFlush(buf);
        heartbeatCount++;
        logger.info("client:{} send ping msg to server:{}, count:{}", new String[]{name, context.channel().remoteAddress().toString(), heartbeatCount+""});
    }

    /**
     * 发送pong消息
     * @param context
     */
    private void sendPongMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(Constants.PONG_MSG);
//        buf.writeBytes("\n".getBytes());
        context.channel().writeAndFlush(buf);
        heartbeatCount++;
        logger.info("server:{} send pong msg to client:{}, count:{}", new String[]{name, context.channel().remoteAddress().toString(), heartbeatCount+""});
    }

    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.error("{} active!", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.error("{} inactive!", ctx.channel().remoteAddress());
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        logger.error("READER_IDLE");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        logger.error("WRITER_IDLE");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        logger.error("ALL_IDLE");
    }
}
