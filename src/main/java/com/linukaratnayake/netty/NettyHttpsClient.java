package com.linukaratnayake.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;

public class NettyHttpsClient {
    private final String host;
    private final int port;
    private final URI uri;
    private final SslContext sslContext;

    public NettyHttpsClient(String host, int port, String protocol) throws URISyntaxException, SSLException {
        this.host = host;
        this.port = port;  // Default HTTPS port
        this.uri = new URI(protocol + "://" + host);
        this.sslContext = SslContextBuilder.forClient().build();  // Build the SSL context for client-side SSL
    }

    public void get() throws Exception {
        // Configure the EventLoop for handling events
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // Bootstrap the client
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) {
                            // Add SSL handler to the pipeline
                            ch.pipeline().addFirst(sslContext.newHandler(ch.alloc(), host, port));

                            // Add HTTP client handlers (for encoding/decoding HTTP)
                            ch.pipeline().addLast(
                                    new HttpClientCodec(),  // Handles encoding/decoding HTTP
                                    new HttpObjectAggregator(1024 * 1024),  // Aggregates HTTP responses
                                    new SimpleChannelInboundHandler<FullHttpResponse>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
                                            // Print out the response
                                            System.out.println("Response from " + host + ": ");
                                            System.out.println(msg.content().toString(CharsetUtil.UTF_8));
                                            ctx.close();  // Close the channel after receiving the response
                                        }
                                    }
                            );
                        }
                    });

            // Connect to the server
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

            // Create and send the HTTP GET request
            FullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toString());

            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

            // Send the HTTP request
            channelFuture.channel().writeAndFlush(request).sync();

            // Wait for the response to be received
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();  // Shut down the EventLoop group
        }
    }
}