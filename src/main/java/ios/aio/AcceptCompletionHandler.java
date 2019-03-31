package ios.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
//CompletionHandler<V result, A attachment>用于处理异步IO操作的结果的操作
//IO操作成功完成后，调用completed()方法,IO操作失败，调用failed()方法
//result IO操作的结果(初始化一个AsynchronousSocketChannel)， attachment  启动时附加到I / O操作的对象
public class AcceptCompletionHandler 
	implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

	//第一个参数是处理结果
	//第二个参数是附加的对象
	//因为accepte()方法的结果是创建一个AsynchronousSocketChannel对象
	//附加AsyncTimeServerHandler对象是为了继续异步监听信道请求
	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
		attachment.asynchronousServerSocketChannel.accept(attachment, this);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		//读操作完成后调用ReadCompletionHandler的completed()方法
		//read()方法的两个参数是同一个buffer，即指同一个缓冲区，因此read把AsynchronousSocketChannel的数据
		//读到第一个buffer中后，将第二个buffer传入到ReadCompletionHandler中进行处理
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

	
}
