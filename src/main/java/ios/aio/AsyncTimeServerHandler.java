package ios.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable{
	
	private int port;
	
	CountDownLatch latch;
	
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;
	
	public AsyncTimeServerHandler(int port) {
		this.port = port;
		try {
			//创建异步服务器套接字信道
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			//绑定到网络地址
			asynchronousServerSocketChannel.bind(new InetSocketAddress("127.0.0.1", port));
			System.out.println("The time server is start in port:" + port);
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		//等待一个线程结束
		latch = new CountDownLatch(1);
		doAccept();
		try {
			//等待一个线程结束
			latch.await();
			//这里往后都需要等待一个线程结束后才可以执行
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void doAccept() {
		//该方法启动一个异步操作，用于接受信道套接字产生的连接请求
		//accept(A attachment,CompletionHandler<AsynchronousSocketChannel, ? super A> handler)
		//第一个参数是附加给AcceptCompletionHandler的对象，用于调用completed()方法时处理
		//第二个参数AcceptCompletionHandler的第一个参数是AsynchronousSocketChannel,
		//是因为accept()方法的结果是初始化一个AsynchronousSocketChannel
		asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
	}

}
