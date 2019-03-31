package ios.io;
/**
 * BIO
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements Runnable{
	private Socket socket = null;

	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			String currentTime = null;
			String body = null;

			while (true) {
				body = in.readLine();
				if(body == null) {
					break;
				}
				System.out.println("The time server receive order: " + body);
				currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? 
						new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
				out.println(currentTime);		

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out!=null) {
				out.close();
			}
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socket = null;
		}
	}

}
