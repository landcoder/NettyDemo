package com.mustang.netty.demo.client;

import com.mustang.netty.common.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 客户端
 * Created by Mustang on 17/1/16.
 */
public class Client {

    private Logger logger = LoggerFactory.getLogger(getClass());

    NioEventLoopGroup workGroup = new NioEventLoopGroup(5);
    private Channel channel;
    private Bootstrap bootstrap;

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.start();
        client.sendData("test msg...");
    }

    /**
     * 启动客户端
     */
    public void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientInitializer(Client.this));
            doConnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 像服务端发送消息
     * @param msg 消息体
     * @throws InterruptedException
     */
    public void sendData(String msg) throws InterruptedException {
        if (channel != null && channel.isActive()) {
            logger.info(msg);
            ByteBuf buf = channel.alloc().buffer(5 + msg.getBytes().length);
            buf.writeInt(5 + msg.getBytes().length);
            buf.writeByte(Constants.CUSTOM_MSG);
            buf.writeBytes(msg.getBytes());
            channel.writeAndFlush(buf);
        } else {
            Thread.sleep(1000);
            sendData(msg);
        }
    }

    /**
     * 客户端连接服务端
     */
    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect(Constants.HOST, Constants.PORT);
        logger.info("bind host:{}, bind port:{}" , Constants.HOST, Constants.PORT);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    logger.info("connect success!");
                } else {
                    logger.info("connect fail, try again 10s later!");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }

}
