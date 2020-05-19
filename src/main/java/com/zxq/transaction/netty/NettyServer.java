package com.zxq.transaction.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * netty服务端
 *
 * @author zhaoxiqing
 */
@Slf4j
public class NettyServer {
    private int port = 20803;
    /**
     * 维护设备在线的表
     */
    private Map<String, Integer> clientMap = new HashMap<>();

    /**
     * 维护设备连接的map 用于推送消息
     */
    private Map<String, Channel> channelMap = new HashMap<>();


    public synchronized void setClient(String name) {
        this.clientMap.put(name, 1);
    }

    public synchronized void removeClient(String name) {
        this.clientMap.remove(name);
    }

    /**
     * 判断连接处里面是否有东西
     *
     * @return
     */
    public synchronized boolean getClientMapSize() {
        return this.clientMap.size() > 0;
    }


    public synchronized void setChannel(String name, Channel channel) {
        this.channelMap.put(name, channel);
    }

    public synchronized Map<String, Channel> getChannelMap() {
        return this.channelMap;
    }

    /**
     * 发送消息给下游设备
     *
     * @param msg
     * @return
     */
    public boolean writeMsg(String msg) {
        boolean errorFlag = false;
        Map<String, Channel> channelMap = getChannelMap();
        if (channelMap.size() == 0) {
            return true;
        }
        Set<String> keySet = clientMap.keySet();
        for (String key : keySet) {
            try {
                Channel channel = channelMap.get(key);
                if (!channel.isActive()) {
                    errorFlag = true;
                    continue;
                }
                channel.writeAndFlush(msg + System.getProperty("line.separator"));
            } catch (Exception e) {
                errorFlag = true;
            }
        }
        return errorFlag;
    }

    public void bind() {
        System.out.println("service start successful");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //特殊分隔符
                        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,
                                Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes())));
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast("handler", new NettyServerHandler(NettyServer.this));
                    }
                });
        try {
            ChannelFuture f = bootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    class NettyServerHandler extends SimpleChannelInboundHandler {
        private int counter = 0;
        private NettyServer nettyServer;

        public NettyServerHandler(NettyServer nettyServer) {
            this.nettyServer = nettyServer;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            System.out.println("client say" + o.toString());
            //重置心跳次数
            counter = 0;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String clientName = ctx.channel().remoteAddress().toString();
            System.out.println("RemoteAddress:" + clientName + "active!");
            nettyServer.setClient(clientName);
            nettyServer.setChannel(clientName, ctx.channel());
            super.channelActive(ctx);
            counter = 0;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.READER_IDLE)) {
                    //空闲4s后触发
                    if (counter >= 10) {
                        ctx.channel().close().sync();
                        String clientName = ctx.channel().remoteAddress().toString();
                        System.out.println("" + clientName + "offline");
                        nettyServer.removeClient(clientName);
                        //判断是否有在线的
                        if (nettyServer.getClientMapSize()) {
                            return;
                        }
                    } else {
                        counter++;
                        System.out.println("loss" + counter + "count HB");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        final NettyServer nettyServer = new NettyServer();
        new Thread() {
            @Override
            public void run() {
                nettyServer.bind();
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        String msg = "";
        while (!(msg = scanner.nextLine()).equals("exit")) {
            System.out.println(nettyServer.writeMsg(msg));
        }
    }
}
