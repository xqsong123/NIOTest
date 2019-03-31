package netty.serialize.protobuf;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import netty.serialize.protobuf.data.SubscribeReq;
import netty.serialize.protobuf.data.SubscribeResp;

@ChannelHandler.Sharable
public class SubReqClientHandler extends ChannelHandlerAdapter {

    public  SubReqClientHandler(){

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.write(createRequest(i));
        }
        ctx.flush();
    }
    private SubscribeReq.SubsribeReq createRequest(int id){
        SubscribeReq.SubsribeReq.Builder builder = SubscribeReq.SubsribeReq.newBuilder();
        builder.setSubReqID(id);
        builder.setUserName("username" + id);
        builder.setProductName("product" + id);
        builder.setAddress("address" + id);
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeResp.SubsribeResp resp = (SubscribeResp.SubsribeResp)msg;
        System.out.println("Client receive Server response: " + resp.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
