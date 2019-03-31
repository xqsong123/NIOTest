package netty.time;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {

	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			//设置多线程事件循环，用于处理ServerChannel与Channel之间的事件与IO操作
			//boss用于接收连接请求，work用于处理IO操作
			b.group(bossGroup, workerGroup).
				//实例化一个NioServerSocketChannel类
				channel(NioServerSocketChannel.class).
				//为刚才实例化的NioServerSocketChannel通道设置参数
				option(ChannelOption.SO_BACKLOG, 1024).
				//设置childHandler，用于处理Channel的请求
				childHandler(new ChildChannelHandler());
			//绑定端口，同步等待成功
			//ChannelFuture是异步IO操作的返回结果
			 ChannelFuture f = b.bind(port).sync();
			 //等待服务器监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	//ChannelInitializer<?>是ChannelInitializer<SocketChannel>因为
	//ServerSocketChannel接收到连接请求后，实例化一个SocketChannel
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception{
//			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
//			ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//			arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
			arg0.pipeline().addLast(new StringDecoder());
			arg0.pipeline().addLast(new TimeServerHandler());
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if(args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		new TimeServer().bind(port);
	}


}
