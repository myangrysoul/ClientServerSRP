package ClientField;

import SafeContext.SRP;
import Wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handler implementation for the object echo client.  It initiates the
 * ping-pong traffic between the object echo client and server by sending the
 * first message to the server.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    //private final List<Integer> firstMessage;
    public  final ArrayList<String> firstMessage=new ArrayList<String>();
    private ClientField client;


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

    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws RuntimeException{
        String id="";
        String pass="";
        Scanner in=new Scanner(System.in);
        System.out.println("Enter Username");
        if(in.hasNextLine()){
            id=in.nextLine();
        }
        System.out.println("Enter pass");
        if(in.hasNextLine()){
            pass=in.nextLine();
        }
        if(id.length()>3&&pass.length()>3){
            client=new ClientField(id,pass);
            System.out.println(SRP.getN()+" "+SRP.getG());
        }
        else{
            ctx.close();
            throw new RuntimeException("Short pass or ID");
        }
        ArrayList<Object> data=new ArrayList<Object>();
        data.add(client.getId());
        data.add(client.getSalt());
        data.add(client.getPass_verifier());
        Wrapper wrapper=new Wrapper(1,data,null);
        ctx.write(wrapper);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.flush();
        ArrayList<Object> data=new ArrayList<Object>();
        data.add(client.getId());
        data.add(client.compA());
        ctx.write(new Wrapper(2,data,null));
        ctx.flush();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String) {
            System.out.println(msg);
        }
        else {
            Wrapper msg1 = (Wrapper) msg;
            switch (msg1.getStage()) {
            case 1:
                String str = (String) msg1.getObject();
                str = str.split(client.getSalt())[0];
                long b = Long.parseLong(str);
                if (b != 0) {
                    client.setbBig(b);
                    if (!"0".equals(client.scrambler())) {
                        client.keyComp();
                        System.out.println(client.key);
                        String a = client.confirmationHash();
                        System.out.println(a);
                        ArrayList<Object> data = new ArrayList<Object>();
                        data.add(client.getId());
                        data.add(a);
                        ctx.write(new Wrapper(3, data, null));
                    } else {
                        ctx.close();
                        throw new RuntimeException("1");
                    }
                } else {

                    ctx.close();
                    throw new RuntimeException("2");
                }

                break;
            case 2:
                String s=(String) msg1.getObject();
                if(s.equals(client.getM())){
                    ArrayList<Object> data = new ArrayList<Object>();
                    data.add(client.getId());
                    data.add(client.compR());
                    ctx.write(new Wrapper(4,data,null));
                }
                else{
                    ctx.close();
                    throw new RuntimeException("3");
                }
                break;
            case 3:
                if(msg1.getObject().equals(client.getR())){
                    System.out.println("Its real server");
                }
                else{

                    ctx.close();
                    throw new RuntimeException("4");
                }
                break;

            }
        }

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