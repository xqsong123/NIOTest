package ios.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable{
	private Selector selector;
	private ServerSocketChannel serverChannel; 
	private volatile boolean stop;
	private long startTime;
	private long endTime;
	private long start;
	private long end;
	
	public MultiplexerTimeServer(int port) {
		try {
			
			//开启/实例化一个Selector
			selector = Selector.open();
			//开启/实例化一个ServerSocketChannel
			serverChannel = ServerSocketChannel.open();
			//设置阻塞的模式
			serverChannel.configureBlocking(false);
			//ServerSocket绑定网络地址
			serverChannel.socket().bind(new InetSocketAddress(port), 1024);
			//将ServerSocketChannel注册到Selector，并设置Selector监听该通道的“连接事件”
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port:" + port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop() {
		this.stop = true;
	}

	public void run() {
		while (!stop) {
			try {
				startTime = System.currentTimeMillis();
				System.out.println("开始检测：" + startTime);
				//检测监听的事件是否发生，该方法是阻塞的，这里最长阻塞1000ms。如果检测到事件发生、超时或者出现异常时，检测线程被中断,向下执行
				//线程在阻塞期间不停地检测事件
				//如果某个通道有事件发生，将该通道的Key加入到selectedKeys的set中
				//如果没有事件发生 ，这个while循环每秒钟循环一次
				selector.select(1000);
				endTime = System.currentTimeMillis();
				System.out.println("检测到事件：" + endTime);
				System.out.println("检测到事件-开始检测：" + (endTime - startTime)/Math.pow(10, 3));
				System.out.println("-----------------------------------------------");
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				while(it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handlerInput(key);
					} catch(Exception e) {
						if(key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				
			}
		}
	}
	
	private void handlerInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			//如果key关联的通道（ServerSocketChannel）已经准备好接受一个连接请求
			if (key.isAcceptable()) {
				//根据key获取ServerSocketChannel
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				//服务端实例化一个SocketChannel（SocketChannel也与key关联）
				SocketChannel sc = ssc.accept();
				//设置SocketChannel的阻塞的模式
				sc.configureBlocking(false);
				//将SocketChannel注册的selector中，监听SocketChannel的“读事件”
				start = System.currentTimeMillis();
				System.out.println("注册监听客户端通道“读事件”：" + startTime);
				sc.register(selector, SelectionKey.OP_READ);
			}
			//如果key关联的通道发生了读事件，（在当前的selelctor.selector（）检测到的还是后续的？）
			if (key.isReadable()) {
				end = System.currentTimeMillis();
				System.out.println("发生客户端通道“读事件”：" + System.currentTimeMillis());
				System.out.println("时差：" + (end-start)/Math.pow(10, 3));
				System.out.println("-----------------------------------------------");
				//获取key关联的SocketChannel通道
				SocketChannel sc = (SocketChannel)key.channel();
				//实例化一个缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				//从SocketChannel通道中读数据到缓冲区
				int readBytes = sc.read(readBuffer);
				if (readBytes > 0) {
					//将缓冲区（从写模式）变为读模式////前面是SocketChannel写到缓冲区，现在需要将缓冲区数据读出来
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					//从缓冲区读数据
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order:" + body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? 
							new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
					doWrite(sc, currentTime);
				} else if (readBytes < 0) {
					key.cancel();
					sc.close();
				} else {
					;
				}
			}
			System.out.println("handlerInput方法出来了");
		} 
	}
	
	private void doWrite(SocketChannel channel, String response) throws IOException {
		if (response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			//构造一个与响应大小相同的缓冲区
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			//向缓冲区写数据
			writeBuffer.put(bytes);
			//将缓冲区从写模式改为读模式
			writeBuffer.flip();
			//从缓冲区读数据到通道（通道将缓冲区数据写入自身）
			channel.write(writeBuffer);
		}
	}
}
