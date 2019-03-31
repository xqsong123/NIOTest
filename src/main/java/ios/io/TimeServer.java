package ios.io;
/**
 * BIO
 * @author songx
 *
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
	public static void main(String[] args) throws IOException {
		int port = 8080;
		if (args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
		}

		ServerSocket server = null;

		try {
			server = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
			Socket socket = null;
			while (true) {
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				server.close();
				server = null;
			}
		}


	}
}
