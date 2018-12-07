package ClientField;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler implementation for the object echo client.  It initiates the
 * ping-pong traffic between the object echo client and server by sending the
 * first message to the server.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    //private final List<Integer> firstMessage;
    public  final ArrayList<String> firstMessage=new ArrayList<String>();
    /**
     * Creates a client-side handler.
     */
    public ClientHandler() {
        /*
        firstMessage = new ArrayList<Integer>(ClientField.ObjectEchoClient.SIZE);
        for (int i = 0; i < ClientField.ObjectEchoClient.SIZE; i ++) {
        firstMessage.add(Integer.valueOf(i));
        }
        */
        firstMessage.add("netty");
        firstMessage.add("hueta");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message if this handler is a client-side handler.
        ctx.write(firstMessage);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        firstMessage.add("228");
        // Echo back the received object to the server.
        ctx.write(firstMessage);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}