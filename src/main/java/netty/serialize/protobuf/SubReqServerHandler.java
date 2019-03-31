package netty.serialize.protobuf;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import netty.serialize.protobuf.data.SubscribeReq;
import netty.serialize.protobuf.data.SubscribeResp;

@ChannelHandler.Sharable
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq.SubsribeReq req = (SubscribeReq.SubsribeReq) msg;
        System.out.println("Server receive client subscribe request:" + req.toString());
        ctx.writeAndFlush(createResponse(req));
    }

    private SubscribeResp.SubsribeResp createResponse(SubscribeReq.SubsribeReq subsribeReq){
        SubscribeResp.SubsribeResp.Builder builder = SubscribeResp.SubsribeResp.newBuilder();
        builder.setSubReqID(subsribeReq.getSubReqID());
        builder.setRespCode(0);
        builder.setDesc(subsribeReq.toString());
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       cause.printStackTrace();
       ctx.close();
    }

}
