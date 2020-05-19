package com.zxq.transaction.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * netty客户端
 *
 * @author zhaoxiqing
 */
@Slf4j
public class NettyClient {
    private String host;
    private int port;
    private Channel channel;
    private Bootstrap b = null;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
        b = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup).option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,
                                Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes())));
                        //字符串编码解码
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        //心跳检测
                        pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                        //客户端的逻辑
                        pipeline.addLast("handler", new NettyClientHandler(NettyClient.this));

                    }
                });
    }

    public void start() {
        ChannelFuture f = b.connect(host, port);
        //断线重连
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    final EventLoop loop = channelFuture.channel().eventLoop();
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("not connect service");
                            start();
                        }
                    }, 1L, TimeUnit.SECONDS);
                } else {
                    channel = channelFuture.channel();
                    System.out.println("connected");
                }
            }
        });
    }

    public Channel getChannel() {
        return channel;
    }

    class NettyClientHandler extends SimpleChannelInboundHandler {
        private NettyClient nettyClient;
        private String tenantId;
        private int attempts = 0;

        public NettyClientHandler(NettyClient nettyClient) {
            this.nettyClient = nettyClient;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            System.out.println("service send message" + o.toString());
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("output connected!");
            attempts = 0;
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("offline");
            //使用过程中断线重连
            final EventLoop eventLoop = ctx.channel().eventLoop();
            if (attempts < 12) {
                attempts++;
            }
            int timeout = 2 << attempts;
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    nettyClient.start();
                }
            }, timeout, TimeUnit.SECONDS);
            ctx.fireChannelInactive();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.READER_IDLE)) {
                    System.out.println("READER_IDLE");
                } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                    //发送心跳，保持长连接
                    String s = "NettyClient" + System.getProperty("line.separator");
                    ctx.channel().writeAndFlush(s);  //发送心跳成功
                } else if (event.state().equals(IdleState.ALL_IDLE)) {
                    System.out.println("ALL_IDLE");
                }
            }
            super.userEventTriggered(ctx, evt);
        }
    }

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 20803);
        nettyClient.start();
    }

}
