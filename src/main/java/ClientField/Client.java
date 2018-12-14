package ClientField;

import Wrapper.Wrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public final class Client {

    private Client(){

    }
    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static ClientField clientField;

    public static void setClientField(ClientField client) {
        clientField = client;
    }

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                                      .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                     }
                     p.addLast(
                             //new LoggingHandler(LogLevel.INFO),
                             new ObjectEncoder(),
                             new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                             new ClientHandler(),
                             // new StringEncoder(),
                             //new StringDecoder(),
                             new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));
                 }
             });

            // Start the connection attempt.
            Channel ch = b.connect(HOST, PORT).sync().channel();
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }


                if ("bye".equals(line.toLowerCase())) {
                    ch.writeAndFlush(line);
                    ch.closeFuture().sync();
                    break;
                } else if (line.startsWith("/add ".toLowerCase())) {
                    lastWriteFuture = ch.writeAndFlush(new Wrapper(20, null, line.substring(5)));
                    ClientField.destination = line.substring(5).toLowerCase();
                } else if (line.startsWith("/stop")) {
                    lastWriteFuture = ch.writeAndFlush(new Wrapper(30, null, null));
                    ClientField.destination = "";
                } else if (!ClientField.destination.isEmpty()) {
                    System.out.println(ClientField.destination);
                    lastWriteFuture = ch.writeAndFlush(clientField.rsaEncode(line));
                } else {
                    System.out.println("Use form \"/add destination_name\" to send a message to some1");
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }

        } finally {
            group.shutdownGracefully();
        }
    }
}