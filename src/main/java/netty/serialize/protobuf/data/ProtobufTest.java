package netty.serialize.protobuf.data;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtobufTest {
    public static void main(String[] args){
        SubscribeReq.SubsribeReq.Builder builder = SubscribeReq.SubsribeReq.newBuilder();
        //序列化
        System.out.println("序列化：");
        builder.setSubReqID(1);
        builder.setUserName("apache");
        builder.setProductName("tomcat");
        builder.setAddress("8080");
        SubscribeReq.SubsribeReq subsribeReq = builder.build();
        System.out.println("protobuf字节：");
        for(Byte b : subsribeReq.toByteArray()){
            System.out.print(b + "  ");
        }
        //反序列化
        System.out.println("反序列化：");
        try {
            SubscribeReq.SubsribeReq subsribeReq1 = SubscribeReq.SubsribeReq.parseFrom(subsribeReq.toByteArray());
            System.out.println(subsribeReq1.getSubReqID());
            System.out.println(subsribeReq1.getUserName());
            System.out.println(subsribeReq1.getProductName());
            System.out.println(subsribeReq1.getAddress());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
