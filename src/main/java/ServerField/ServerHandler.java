package ServerField;

import SafeContext.Key;

import Wrapper.Wrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static ServerField.Server.channelmap;

/**
 * Handles both client-side and server-side handler depending on which constructor was called.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    ServerField server;

    ServerHandler() {
        server = Server.getServer();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      /* if(Server.getServer()!=null&&server.accounts.get(channelmap.get(ctx.channel())).isVerrified()){
            destManage(ctx,msg);
            messageHandle(ctx,msg);
        }
        else {
            Wrapper msg1 = (Wrapper) msg;
            auth(ctx, msg1);
        }
    */
        try {
            if (Server.getServer() != null && server.accounts.get(channelmap.get(ctx.channel())).isVerrified()) {
                destManage(ctx, msg);
                messageHandle(ctx, msg);
                bye(ctx, msg);
            } else {
                Wrapper msg1 = (Wrapper) msg;
                auth(ctx, msg1);
            }
        } catch (NullPointerException e) {
            Wrapper msg1 = (Wrapper) msg;
            auth(ctx, msg1);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.getCause();
        ctx.close();
    }

    private void bye(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String && msg.equals("bye".toLowerCase())) {
            String user = channelmap.get(ctx.channel());
            String dest = server.accounts.get(user).getDestination();
            if (dest.length() != 0) {
                Channel user2ch = keyFromValue(server.accounts.get(user).getDestination());
                String user2 = channelmap.get(keyFromValue(server.accounts.get(user).getDestination()));
                server.accounts.get(user).setDestination("");
                server.accounts.get(user2).setDestination("");
                user2ch.writeAndFlush(new Wrapper(404, null, "Your sobesednik have left"));
            }
            server.accounts.remove(user);
            channelmap.remove(ctx.channel());
            ctx.close();
        }
    }

    private void destManage(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Wrapper && ((Wrapper) msg).getStage() == 20) {
            Wrapper msg1 = (Wrapper) msg;
            if (server.accounts.containsKey(msg1.getObject())) {
                String destination = (String) msg1.getObject();
                String user = channelmap.get(ctx.channel());
                server.accounts.get(user).setDestination(destination);
                server.accounts.get(destination).setDestination(user);
                try {
                    keyFromValue(destination).writeAndFlush(new Wrapper(20, null, destination));
                    keyFromValue(destination)
                            .writeAndFlush(new Wrapper(30, null, server.accounts.get(user).getOpenKey()));
                    ctx.writeAndFlush(new Wrapper(30, null, server.accounts.get(destination).getOpenKey()));
                } catch (NullPointerException e) {
                    ctx.writeAndFlush(new Wrapper(404, null, "No such destination found"));
                }
            }
        } else if (msg instanceof Wrapper && ((Wrapper) msg).getStage() == 30) {
            String user = channelmap.get(ctx.channel());
            Channel user2ch = keyFromValue(server.accounts.get(user).getDestination());
            String user2 = channelmap.get(keyFromValue(server.accounts.get(user).getDestination()));
            server.accounts.get(user).setDestination("");
            server.accounts.get(user2).setDestination("");
            user2ch.writeAndFlush(new Wrapper(404, null, "Your sobesednik have rejected connection"));
        }
    }


    private void messageHandle(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof BigInteger) {
            String user = channelmap.get(ctx.channel());
            if (server.accounts.containsKey(server.accounts.get(user).getDestination())) {
                try {
                    keyFromValue(server.accounts.get(user).getDestination()).writeAndFlush(msg);
                } catch (NullPointerException e) {
                    ctx.writeAndFlush(new Wrapper(404, null, "No such destination found"));
                }
            }

        }
    }

    private void auth(ChannelHandlerContext ctx, Wrapper msg1) {
        ArrayList<Object> data = msg1.getData();
        String user = (String) data.get(0);

        switch (msg1.getStage()) {
        case 1:
            if (server == null) {
                server = new ServerField(user, new Account((Long) data.get(2), (String) data.get(1)),
                                         (Long) data.get(3));
                Server.setServer(server);
            } else {
                server.addUser(user, new Account((Long) data.get(2), (String) data.get(1)),
                               (Long) data.get(3));
                Server.setServer(server);
            }
            channelmap.put(ctx.channel(), user);
            ctx.write("Account have created!!");
            break;
        case 2:
            long aBig = (Long) data.get(1);
            if (aBig != 0) {
                server.receive(user, aBig);
                server.generateB(user);
                ctx.write(new Wrapper(1, null,
                                      server.accounts.get(user).getbBig() + server.accounts.get(user).getUser_salt()));
            } else {
                ctx.close();
                throw new RuntimeException("Error");
            }
            if (!"0".equals(server.scrambler(user))) {
                server.keyCompute(user);
                server.confirmationHash(user);
                ctx.write(new Wrapper(2, null, server.accounts.get(user).getM()));
            } else {
                ctx.close();
            }

            break;
        case 3:
            if (data.get(1).equals(server.accounts.get(user).getM())) {
                server.compR(user);
                ctx.write(new Wrapper(3, null, server.accounts.get(user).getR()));
            }
            break;
        case 4:
            if (data.get(1).equals(server.accounts.get(user).getR())) {
                ctx.writeAndFlush("Hello my client");
                System.out.println("Its real client");
            } else {
                ctx.close();
            }
            break;
        case 5:
            server.accounts.get(user).setOpenKey((Key) data.get(1));
            server.accounts.get(user).setVerrified(true);
            break;
        }
    }

    private static Channel keyFromValue(String value) {
        Set<Entry<Channel, String>> entrySet = channelmap.entrySet();

        for (Map.Entry<Channel, String> pair : entrySet) {
            if (value.equals(pair.getValue())) {
                return pair.getKey();
            }
        }
        throw new NullPointerException("No such destination found");
    }
}
