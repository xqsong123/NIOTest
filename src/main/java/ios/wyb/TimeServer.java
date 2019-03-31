package ios.wyb;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 伪异步,使用线程池，仍是阻塞的
 * @author songx
 *
 */
public class TimeServer {
	public static void main(String[] args) {
		int port = 8080;
		if (args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
			System.out.println("The time server is start in port:" + port);
			Socket socket = null;
			
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);
			
			while(true) {
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
